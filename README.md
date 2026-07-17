# Travel Agency Microservices

Mini projeto de estudos para entender mensageria com RabbitMQ usando Spring Boot.

O sistema simula uma agencia de viagens simples. O cliente cria uma reserva, um servico processa a reserva do pacote e outro servico simula o envio de notificacao.

## Objetivo

Estudar, de forma pequena e pratica:

- producer e consumer
- exchange, queue, binding e routing key
- comunicacao assincrona entre servicos
- evento de resposta
- consistencia eventual
- fluxo simples de sucesso e falha

## Servicos

| Servico | Porta | Responsabilidade |
| --- | ---: | --- |
| `booking-service` | `8081` | API REST, banco de reservas, publica e consome eventos |
| `reservation-service` | `8082` | Consome pedido de reserva e publica sucesso ou falha |
| `notification-service` | `8083` | Consome confirmacao/cancelamento e simula envio de e-mail |

## Infraestrutura

O arquivo `docker-compose.yml` sobe:

| Recurso | Porta local | Uso |
| --- | ---: | --- |
| RabbitMQ | `5672` | Broker AMQP |
| RabbitMQ Management | `15672` | Painel web do RabbitMQ |
| PostgreSQL | `5433` | Banco do `booking-service` |

Credenciais locais:

```text
RabbitMQ
user: guest
password: guest

PostgreSQL
database: travel_booking_db
user: travel
password: travel
```

## Fluxo Planejado

```text
1. Cliente cria reserva no booking-service
2. booking-service salva a reserva como PENDING
3. booking-service publica booking.created
4. reservation-service consome booking.created
5. reservation-service simula a reserva do pacote
6. reservation-service publica booking.reserved ou booking.failed
7. booking-service consome o resultado
8. booking-service atualiza para CONFIRMED ou CANCELLED
9. booking-service publica booking.confirmed ou booking.cancelled
10. notification-service consome o evento final e registra o envio da notificacao
```

## Topologia RabbitMQ Planejada

Exchange:

```text
travel.exchange
```

Filas:

```text
reservation.queue
booking-result.queue
notification.queue
```

Routing keys:

```text
booking.created
booking.reserved
booking.failed
booking.confirmed
booking.cancelled
```

Bindings:

```text
reservation.queue    <- booking.created
booking-result.queue <- booking.reserved
booking-result.queue <- booking.failed
notification.queue   <- booking.confirmed
notification.queue   <- booking.cancelled
```

## Como Rodar

Subir RabbitMQ e PostgreSQL:

```bash
docker compose up -d
```

Acessar o painel do RabbitMQ:

```text
http://localhost:15672
```

Rodar os servicos, em terminais separados:

```bash
cd booking-service
./mvnw spring-boot:run
```

```bash
cd reservation-service
./mvnw spring-boot:run
```

```bash
cd notification-service
./mvnw spring-boot:run
```

## Estado Atual

- Estrutura dos tres servicos criada.
- Docker Compose configurado com RabbitMQ e PostgreSQL.
- Configuracoes locais de porta, banco e RabbitMQ definidas nos `application.properties`.

## Proximos Passos

1. Criar a configuracao RabbitMQ em cada servico.
2. Criar os DTOs dos eventos.
3. Criar a entidade `Booking` no `booking-service`.
4. Criar os endpoints REST do `booking-service`.
5. Implementar os consumers e producers.
