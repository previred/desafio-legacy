-- Eliminar tabla si existe para asegurar un inicio limpio en cada ejecución
DROP TABLE IF EXISTS empleados;

CREATE TABLE empleados (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    rut VARCHAR(20) NOT NULL UNIQUE,
    cargo VARCHAR(100) NOT NULL,
    salario DOUBLE NOT NULL,
    bono DOUBLE DEFAULT 0,
    descuento DOUBLE DEFAULT 0
);

-- (Opcional) Insertar un registro de prueba
INSERT INTO empleados (nombre, apellido, rut, cargo, salario, bono, descuento) 
VALUES ('Marcelo', 'Valderrama', '12345678-9', 'Senior Developer', 2500000, 0, 0);