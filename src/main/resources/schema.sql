CREATE TABLE empleados (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    rut VARCHAR(12) UNIQUE NOT NULL,
    cargo VARCHAR(100) NOT NULL,
    salario_base DECIMAL(12,2) NOT NULL CHECK (salario_base >= 400000),
    bonos DECIMAL(12,2) DEFAULT 0 CHECK (bonos >= 0),
    descuentos DECIMAL(12,2) DEFAULT 0 CHECK (descuentos >= 0),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_rut ON empleados(rut);
