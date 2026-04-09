CREATE TABLE IF NOT EXISTS empleados (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre     VARCHAR(100) NOT NULL,
    apellido   VARCHAR(100) NOT NULL,
    rut        VARCHAR(20)  NOT NULL UNIQUE,
    cargo      VARCHAR(100) NOT NULL,
    salario    DECIMAL(15, 2) NOT NULL,
    bono       DECIMAL(15, 2) NOT NULL DEFAULT 0,
    descuento  DECIMAL(15, 2) NOT NULL DEFAULT 0
);
