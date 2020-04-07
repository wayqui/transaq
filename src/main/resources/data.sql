DROP TABLE IF EXISTS TR_TRANSACTION;

CREATE TABLE TR_TRANSACTION (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  reference VARCHAR(250) NOT NULL,
  account_iban VARCHAR(250) NOT NULL,
  transaction_date VARCHAR(250) DEFAULT NULL,
  amount VARCHAR(250) DEFAULT NULL,
  fee VARCHAR(250) DEFAULT NULL,
  description VARCHAR(250) DEFAULT NULL
);

--INSERT INTO TR_TRANSACTION (reference, account_iban, transaction_date, amount, fee, description) VALUES
--  ('R1', 'IBAN1', current_timestamp, 500.0, 10.0, 'Description 1');