package employeemanagement.employee_management.repository;

import employeemanagement.employee_management.model.Employee;
import employeemanagement.employee_management.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * EmployeeRepository - Repository layer for Employee data access
 * Using Spring Data JPA for database operations
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    /**
     * Find employee by email
     */
    Optional<Employee> findByEmail(String email);

    /**
     * Find employees by department
     */
    List<Employee> findByDepartment(Department department);

    /**
     * Find employees by department ID
     */
    List<Employee> findByDepartmentId(Long departmentId);

    /**
     * Find employees by position
     */
    List<Employee> findByPosition(String position);

    /**
     * Find employees by name containing (case-insensitive search)
     */
    List<Employee> findByNameContainingIgnoreCase(String name);

    /**
     * Find employees by department name (case-insensitive)
     */
    List<Employee> findByDepartment_NameContainingIgnoreCase(String departmentName);

    /**
     * Find employees by name and department name (case-insensitive)
     */
    List<Employee> findByNameContainingIgnoreCaseAndDepartment_NameContainingIgnoreCase(String name, String departmentName);

    /**
     * Check if employee exists by email
     */
    boolean existsByEmail(String email);

    /**
     * Count total employees
     */
    @Query("SELECT COUNT(e) FROM Employee e")
    Long countTotalEmployees();

    /**
     * Count employees by department
     */
    @Query("SELECT e.department.name as departmentName, COUNT(e) as count " +
           "FROM Employee e " +
           "GROUP BY e.department.id, e.department.name " +
           "ORDER BY COUNT(e) DESC")
    List<Object[]> countEmployeesByDepartment();

    /**
     * Get employee statistics by position
     */
    @Query("SELECT e.position as position, COUNT(e) as count " +
           "FROM Employee e " +
           "GROUP BY e.position " +
           "ORDER BY COUNT(e) DESC")
    List<Object[]> countEmployeesByPosition();
}