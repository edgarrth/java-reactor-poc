# Requests/Response del servicio

Esta PoC no expone endpoints HTTP porque el objetivo es Reactor sin WebFlux. El contrato operativo es por CLI/worker.

## Levantar infraestructura
```bash
cd infraestructura/docker
docker compose up -d
```

## Ejecutar batch reactivo
```bash
mvn spring-boot:run
```

## Consultar resultados
```bash
docker exec -it reactor-only-payment-postgres psql -U payments -d paymentsdb -c "select id, amount, status, attempts, failure_reason from payments order by created_at;"
docker exec -it reactor-only-payment-postgres psql -U payments -d paymentsdb -c "select payment_id, event_type, payload from payment_events order by created_at;"
```

## Response esperado en consola
```text
Starting Reactor-only payment batch
Processed payments in tick: 5
Batch completed
```
