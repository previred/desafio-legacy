CREATE TABLE empleados (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100),
    apellido VARCHAR(100),
    dni VARCHAR(50),
    cargo VARCHAR(100),
    salario DOUBLE,
    bono DOUBLE,
    descuentos DOUBLE
);