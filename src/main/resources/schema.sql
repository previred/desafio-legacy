CREATE TABLE IF NOT EXISTS empleados (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    rut_dni VARCHAR(50) NOT NULL,
    cargo VARCHAR(100) NOT NULL,
    salario_base DECIMAL(15,2) NOT NULL,
    bono DECIMAL(15,2) DEFAULT 0,
    descuentos DECIMAL(15,2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_empleados_rut_dni UNIQUE (rut_dni)
);
