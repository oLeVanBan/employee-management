package employeemanagement.employee_management.service;

import employeemanagement.employee_management.model.Department;
import employeemanagement.employee_management.model.Employee;
import employeemanagement.employee_management.repository.DepartmentRepository;
import employeemanagement.employee_management.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * DepartmentService - Business logic layer for Department management
 */
@Service
@Transactional
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public DepartmentService(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    /**
     * Create a new department
     */
    public Department createDepartment(Department department) {
        // Check if department with same name already exists
        if (departmentRepository.existsByName(department.getName())) {
            throw new IllegalArgumentException("Department with name '" + department.getName() + "' already exists");
        }
        return departmentRepository.save(department);
    }

    /**
     * Get department by ID
     */
    public Optional<Department> getDepartmentById(Long id) {
        return departmentRepository.findById(id);
    }

    /**
     * Get department by name
     */
    public Optional<Department> getDepartmentByName(String name) {
        return departmentRepository.findByName(name);
    }

    /**
     * Get all departments
     */
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    /**
     * Update department
     */
    public Department updateDepartment(Long id, Department updatedDepartment) {
        Department existing = departmentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Department not found with id: " + id));

        // Check if new name conflicts with another department
        if (!existing.getName().equals(updatedDepartment.getName())) {
            if (departmentRepository.existsByName(updatedDepartment.getName())) {
                throw new IllegalArgumentException("Department with name '" + updatedDepartment.getName() + "' already exists");
            }
        }

        existing.setName(updatedDepartment.getName());
        existing.setDescription(updatedDepartment.getDescription());

        return departmentRepository.save(existing);
    }

    /**
     * Delete department
     */
    public boolean deleteDepartment(Long id) {
        if (departmentRepository.existsById(id)) {
            departmentRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Get total department count
     */
    public long getDepartmentCount() {
        return departmentRepository.count();
    }

    /**
     * Get all employees in a department
     */
    public List<Employee> getEmployeesByDepartment(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
            .orElseThrow(() -> new IllegalArgumentException("Department not found with id: " + departmentId));

        return employeeRepository.findByDepartment(department);
    }
}
