# Travel Agency Microservices

Mini projeto de estudos e portfolio com Spring Boot, RabbitMQ, PostgreSQL e Docker Compose, simulando o fluxo assincrono de uma agencia de viagens.

O objetivo e demonstrar comunicacao entre microservicos, mensageria, persistencia, validacoes, tratamento de erros, idempotencia no consumo de eventos e notificacao por email em ambiente local.

## Arquitetura

| Servico | Porta | Responsabilidade |
| --- | ---: | --- |
| `booking-service` | `8081` | API REST, persiste reservas, publica eventos e processa resultados |
| `reservation-service` | `8082` | Consome reservas criadas e simula confirmacao ou falha |
| `notification-service` | `8084` | Consome status final e envia email via SMTP local |

Infraestrutura local:

| Recurso | Porta | Uso |
| --- | ---: | --- |
| RabbitMQ | `5672` | Broker AMQP |
| RabbitMQ Management | `15672` | Interface web do RabbitMQ |
| PostgreSQL | `5433` | Banco do `booking-service` |
| Mailpit SMTP | `1025` | Servidor SMTP local para testes |
| Mailpit UI | `8025` | Caixa de entrada para visualizar emails |

Credenciais locais:

```text
RabbitMQ: guest / guest
PostgreSQL: travel / travel
Database: travel_booking_db
```

## Fluxo Principal

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
10. notification-service consome o evento final
11. notification-service envia email para o Mailpit
```

## Mensageria

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
  "customerEmail": "danilo@email.com",
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

## Como Rodar

Na raiz do projeto:

```bash
docker compose up -d --build
```

Verificar containers:

```bash
docker compose ps
```

Acessos locais:

```text
API: http://localhost:8081
RabbitMQ Management: http://localhost:15672
Mailpit: http://localhost:8025
```

Ver logs:

```bash
docker compose logs -f booking-service
docker compose logs -f reservation-service
docker compose logs -f notification-service
```

Parar a stack:

```bash
docker compose down
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

## Qualidade da API

O `booking-service` possui:

- DTOs para entrada e saida de dados
- validacao de campos obrigatorios, tamanho, email e quantidade de viajantes
- tratamento global de erros
- resposta `404` para reserva inexistente
- resposta `400` para UUID invalido, JSON malformado e erros de validacao
- testes unitarios para regras principais do servico e listener

## Idempotencia

O `booking-service` controla idempotencia ao consumir `BookingResultEvent`.

Quando um resultado de reserva chega:

```text
1. verifica se o eventId ja existe em processed_events
2. se existir, ignora o evento duplicado
3. se nao existir, atualiza o booking
4. publica o evento de status alterado
5. registra o eventId como processado
```

## Email Local

O envio de email e feito pelo `notification-service` usando `JavaMailSender`.

No Docker Compose, o servico usa o Mailpit como SMTP local:

```text
SMTP: mailpit:1025
Inbox: http://localhost:8025
```

Isso permite validar o envio de email sem usar credenciais reais e sem custo externo.

## Testes

Rodar testes de cada servico:

```bash
cd booking-service
./mvnw test
```

```bash
cd reservation-service
./mvnw test
```

```bash
cd notification-service
./mvnw test
```

## Stack

- Java 17
- Spring Boot
- Spring Web
- Spring AMQP
- Spring Data JPA
- Spring Validation
- Spring Mail
- RabbitMQ
- PostgreSQL
- Mailpit
- Docker Compose
- JUnit
- Mockito
