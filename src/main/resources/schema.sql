CREATE TABLE employee (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    document_number VARCHAR(50) NOT NULL UNIQUE,
    position VARCHAR(100) NOT NULL
);

CREATE TABLE compensation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL UNIQUE,
    base_salary DOUBLE NOT NULL,
    bonus DOUBLE NOT NULL,
    discounts DOUBLE NOT NULL,
    CONSTRAINT fk_compensation_employee
        FOREIGN KEY (employee_id) REFERENCES employee(id)
        ON DELETE CASCADE
);