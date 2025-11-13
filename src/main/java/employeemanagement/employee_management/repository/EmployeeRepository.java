package employeemanagement.employee_management.repository;

import employeemanagement.employee_management.model.Employee;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * EmployeeRepository - Repository layer for Employee data access
 * Demonstrates @Repository annotation
 *
 * In a real application, this would interact with a database.
 * For this demo, we use an in-memory Map.
 */
@Repository
public class EmployeeRepository {

    // In-memory storage (simulating database)
    private final Map<String, Employee> employeeStore = new ConcurrentHashMap<>();

    /**
     * Save or update an employee
     *
     * @param employee Employee to save
     * @return Saved employee
     */
    public Employee save(Employee employee) {
        employeeStore.put(employee.getId(), employee);
        return employee;
    }

    /**
     * Find employee by ID
     *
     * @param id Employee ID
     * @return Optional containing employee if found
     */
    public Optional<Employee> findById(String id) {
        return Optional.ofNullable(employeeStore.get(id));
    }

    /**
     * Find all employees
     *
     * @return List of all employees
     */
    public List<Employee> findAll() {
        return new ArrayList<>(employeeStore.values());
    }

    /**
     * Find employees by department
     *
     * @param department Department name
     * @return List of employees in the department
     */
    public List<Employee> findByDepartment(String department) {
        return employeeStore.values().stream()
            .filter(emp -> emp.getDepartment() != null && emp.getDepartment().equalsIgnoreCase(department))
            .toList();
    }

    /**
     * Delete employee by ID
     *
     * @param id Employee ID
     * @return true if deleted, false if not found
     */
    public boolean deleteById(String id) {
        return employeeStore.remove(id) != null;
    }

    /**
     * Check if employee exists
     *
     * @param id Employee ID
     * @return true if exists
     */
    public boolean existsById(String id) {
        return employeeStore.containsKey(id);
    }

    /**
     * Count all employees
     *
     * @return Total number of employees
     */
    public long count() {
        return employeeStore.size();
    }

    /**
     * Delete all employees
     */
    public void deleteAll() {
        employeeStore.clear();
    }
}
