DROP TABLE IF EXISTS empleados;

CREATE TABLE empleados (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    rut_dni VARCHAR(20) NOT NULL,
    cargo VARCHAR(100) NOT NULL,
    salario_base DECIMAL(15, 2) NOT NULL,
    bono DECIMAL(15, 2) NOT NULL,
    descuentos DECIMAL(15, 2) NOT NULL,
    CONSTRAINT uq_empleados_rut_dni UNIQUE (rut_dni)
);
