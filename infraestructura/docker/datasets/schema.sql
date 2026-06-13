DROP TABLE IF EXISTS payment_events;
DROP TABLE IF EXISTS payments;
CREATE TABLE payments (
  id UUID PRIMARY KEY,
  merchant_id VARCHAR(80) NOT NULL,
  customer_id VARCHAR(80) NOT NULL,
  amount NUMERIC(14,2) NOT NULL,
  currency VARCHAR(3) NOT NULL,
  status VARCHAR(30) NOT NULL,
  attempts INT NOT NULL DEFAULT 0,
  failure_reason VARCHAR(250),
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE TABLE payment_events (
  id UUID PRIMARY KEY,
  payment_id UUID NOT NULL REFERENCES payments(id),
  event_type VARCHAR(60) NOT NULL,
  payload TEXT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_payments_status ON payments(status);
