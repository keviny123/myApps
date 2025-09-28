CREATE TABLE IF NOT EXISTS customer_identity (
  id SERIAL PRIMARY KEY,
  first_name VARCHAR(100),
  last_name VARCHAR(100),
  gender VARCHAR(32),
  dob DATE,
  ssn VARCHAR(32) UNIQUE
);
