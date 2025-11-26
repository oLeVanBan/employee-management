package employeemanagement.employee_management.repository;

import employeemanagement.employee_management.dto.DepartmentStatistics;
import employeemanagement.employee_management.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    /**
     * Count total departments
     */
    @Query("SELECT COUNT(d) FROM Department d")
    Long countTotalDepartments();

    /**
     * Get departments with employee count
     */
    @Query("SELECT d.name as departmentName, " +
           "COUNT(e) as employeeCount " +
           "FROM Department d LEFT JOIN d.employees e " +
           "GROUP BY d.id, d.name " +
           "ORDER BY COUNT(e) DESC")
    List<DepartmentStatistics> getDepartmentsWithEmployeeCount();
}
