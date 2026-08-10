# Requests/Response del servicio

Esta PoC no expone endpoints HTTP porque el objetivo es Reactor Core sin WebFlux. El contrato operativo es por CLI/worker.

## Levantar infraestructura

```bash
cd infraestructura/docker
docker compose up -d
```

Si quieres repetir la prueba desde el dataset original:

```bash
docker compose down -v --remove-orphans
docker compose up -d
```

## Ejecutar batch reactivo

Desde la raíz del proyecto:

```bash
mvn spring-boot:run
```

## Request 1: consultar pagos procesados

```bash
docker exec -it reactor-only-payment-postgres \
  psql -U payments -d paymentsdb \
  -c "select id, amount, status, attempts, failure_reason from payments order by created_at;"
```

## Request 2: consultar eventos generados

```bash
docker exec -it reactor-only-payment-postgres \
  psql -U payments -d paymentsdb \
  -c "select payment_id, event_type, payload, created_at from payment_events order by created_at;"
```

## Response esperado en consola

```text
Starting Reactor-only payment batch
Batch completed
Processed payments in tick: 5
```

No se incluyen comandos `curl` porque el proyecto no levanta un servidor HTTP.
