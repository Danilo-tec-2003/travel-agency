# Travel Agency Microservices

Mini projeto de estudos com Spring Boot e RabbitMQ, simulando o fluxo assincrono de uma agencia de viagens.

O objetivo e demonstrar, de forma simples e bem delimitada, comunicacao entre microservicos, mensageria, persistencia, validacoes, tratamento de erros e idempotencia.

## Arquitetura

| Servico | Porta | Responsabilidade |
| --- | ---: | --- |
| `booking-service` | `8081` | API REST, persiste reservas, publica eventos e processa resultados |
| `reservation-service` | `8082` | Consome reservas criadas e simula confirmacao ou falha |
| `notification-service` | `8084` | Consome status final e simula envio de email por log |

Infraestrutura local:

| Recurso | Porta | Uso |
| --- | ---: | --- |
| RabbitMQ | `5672` | Broker AMQP |
| RabbitMQ Management | `15672` | Interface web |
| PostgreSQL | `5433` | Banco do `booking-service` |

Credenciais locais:

```text
RabbitMQ: guest / guest
PostgreSQL: travel / travel
Database: travel_booking_db
```

## Fluxo

```text
1. Cliente cria uma reserva em POST /bookings
2. booking-service salva a reserva como PENDING
3. booking-service publica BookingCreatedEvent
4. reservation-service consome o evento
5. reservation-service simula o processamento da reserva
6. reservation-service publica BookingResultEvent
7. booking-service consome o resultado
8. booking-service atualiza para CONFIRMED ou CANCELLED
9. booking-service publica BookingStatusChangedEvent
10. notification-service consome o evento final e registra a notificacao
```

## RabbitMQ

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

## API

Base URL:

```text
http://localhost:8081
```

Criar reserva:

```http
POST /bookings
```

```json
{
  "customerName": "Danilo Silva",
  "destination": "Recife",
  "travelers": 2
}
```

Listar reservas:

```http
GET /bookings
```

Buscar reserva por id:

```http
GET /bookings/{id}
```

## Qualidade da API

O `booking-service` possui:

- DTO de entrada: `CreateBookingRequest`
- DTO de saida: `BookingResponse`
- validacao de campos obrigatorios, tamanho e quantidade de viajantes
- tratamento global de erros
- resposta `404` para reserva inexistente
- resposta `400` para UUID invalido, JSON malformado e erros de validacao

## Idempotencia

O `booking-service` possui controle de idempotencia ao consumir `BookingResultEvent`.

Quando um resultado de reserva chega:

```text
1. verifica se o eventId ja existe em processed_events
2. se existir, ignora o evento duplicado
3. se nao existir, atualiza o booking
4. publica o evento de status alterado
5. registra o eventId como processado
```

A tabela usada para controle:

```sql
SELECT *
FROM processed_events
ORDER BY processed_at DESC;
```

## Como Rodar

Subir RabbitMQ e PostgreSQL:

```bash
docker compose up -d
```

Rodar os servicos em terminais separados:

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

Acessar RabbitMQ Management:

```text
http://localhost:15672
```

## Banco

Consultar reservas:

```sql
SELECT *
FROM bookings
ORDER BY created_at DESC;
```

Consultar eventos processados:

```sql
SELECT *
FROM processed_events
ORDER BY processed_at DESC;
```
