DROP TABLE IF EXISTS employees;

CREATE TABLE employees (
   id          BIGINT AUTO_INCREMENT PRIMARY KEY,
   first_name  VARCHAR(100)  NOT NULL,
   last_name   VARCHAR(100)  NOT NULL,
   tax_id      VARCHAR(20)   NOT NULL UNIQUE,
   position    VARCHAR(100)  NOT NULL,
   base_salary DECIMAL(15,2) NOT NULL,
   bonus       DECIMAL(15,2) DEFAULT 0,
   deductions  DECIMAL(15,2) DEFAULT 0,
   created_at  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
   updated_at  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
   deleted_at  TIMESTAMP     DEFAULT NULL
);