package employeemanagement.employee_management.repository;

import employeemanagement.employee_management.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * DepartmentRepository - Repository layer for Department data access
 * Using Spring Data JPA for database operations
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    /**
     * Find department by name
     */
    Optional<Department> findByName(String name);

    /**
     * Check if department exists by name
     */
    boolean existsByName(String name);
}
