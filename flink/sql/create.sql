DROP DATABASE IF EXISTS flink;
CREATE DATABASE flink;

\c flink;
DROP SCHEMA IF EXISTS sales CASCADE;
CREATE SCHEMA sales;

DROP TABLE IF EXISTS sales.orders;
CREATE TABLE sales.orders (
  order_id INT,
  order_date TIMESTAMP(3) NOT NULL,
  customer_id INT NOT NULL,
  total_price DECIMAL(10,2),
  PRIMARY KEY (order_id)
);
