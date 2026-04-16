package cl.previred.rsanchez.demoprevired.repository;

import cl.previred.rsanchez.demoprevired.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Employee findByRut(String rut);

    void deleteById(Long id);
}
