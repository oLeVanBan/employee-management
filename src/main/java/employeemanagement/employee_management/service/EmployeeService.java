package employeemanagement.employee_management.service;

import employeemanagement.employee_management.dto.EmployeeDTO;
import employeemanagement.employee_management.model.Employee;
import employeemanagement.employee_management.model.Department;
import employeemanagement.employee_management.repository.EmployeeRepository;
import employeemanagement.employee_management.repository.DepartmentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * EmployeeService - Business logic layer for Employee management
 * Demonstrates Dependency Injection with Constructor Injection (recommended)
 */
@Service
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final UtilityService utilityService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    /**
     * Constructor Injection - Recommended approach for DI
     * Spring will automatically inject all required beans
     * No need for @Autowired annotation on constructor (since Spring 4.3)
     */
    public EmployeeService(
            EmployeeRepository employeeRepository,
            DepartmentRepository departmentRepository,
            UtilityService utilityService,
            PasswordEncoder passwordEncoder,
            ModelMapper modelMapper
    ) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.utilityService = utilityService;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    /**
     * Create a new employee with auto-generated ID and formatted data
     *
     * @param employee Employee data
     * @return Created employee
     */
    public Employee createEmployee(Employee employee) {
        // Generate employee code using UtilityService
        String employeeCode = utilityService.generateEmployeeCode();
        employee.setId(employeeCode);

        // Format employee name
        String formattedName = utilityService.formatEmployeeName(employee.getName());
        employee.setName(formattedName);

        // Format phone number
        if (employee.getPhone() != null) {
            String formattedPhone = utilityService.formatPhoneNumber(employee.getPhone());
            employee.setPhone(formattedPhone);
        }

        // Validate email
        if (employee.getEmail() != null && !utilityService.isValidEmail(employee.getEmail())) {
            throw new IllegalArgumentException("Invalid email format: " + employee.getEmail());
        }

        return employeeRepository.save(employee);
    }

    /**
     * Get employee by ID
     */
    public Optional<Employee> getEmployeeById(String id) {
        Objects.requireNonNull(id, "Employee id must not be null");
        return employeeRepository.findById(id);
    }

    /**
     * Get all employees
     */
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    /**
     * Search employees by optional name and/or department name filters
     */
    public List<Employee> searchEmployees(String name, String departmentName) {
        String sanitizedName = null;
        String sanitizedDepartment = null;
        boolean hasName = false;
        boolean hasDepartment = false;

        if (name != null) {
            String trimmed = name.trim();
            if (!trimmed.isEmpty()) {
                sanitizedName = trimmed;
                hasName = true;
            }
        }

        if (departmentName != null) {
            String trimmed = departmentName.trim();
            if (!trimmed.isEmpty()) {
                sanitizedDepartment = trimmed;
                hasDepartment = true;
            }
        }

        if (!hasName && !hasDepartment) {
            return getAllEmployees();
        }

        if (hasName && hasDepartment) {
            return employeeRepository
                    .findByNameContainingIgnoreCaseAndDepartment_NameContainingIgnoreCase(
                            sanitizedName,
                            sanitizedDepartment
                    );
        }

        if (hasName) {
            return employeeRepository.findByNameContainingIgnoreCase(sanitizedName);
        }

        return employeeRepository.findByDepartment_NameContainingIgnoreCase(sanitizedDepartment);
    }

    /**
     * Get employees by department
     */
    public List<Employee> getEmployeesByDepartment(Long departmentId) {
        return employeeRepository.findByDepartmentId(departmentId);
    }

    /**
     * Get employees by department name
     */
    public List<Employee> getEmployeesByDepartmentName(String departmentName) {
        Department department = departmentRepository.findByName(departmentName)
            .orElseThrow(() -> new IllegalArgumentException("Department not found: " + departmentName));
        return employeeRepository.findByDepartment(department);
    }

    /**
     * Update employee
     */
    public Employee updateEmployee(String id, Employee updatedEmployee) {
        Objects.requireNonNull(id, "Employee id must not be null");
        Optional<Employee> existing = employeeRepository.findById(id);

        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Employee not found with id: " + id);
        }

        // Keep the same ID
        updatedEmployee.setId(id);

        // Format name if changed
        if (updatedEmployee.getName() != null) {
            updatedEmployee.setName(utilityService.formatEmployeeName(updatedEmployee.getName()));
        }

        // Format phone if changed
        if (updatedEmployee.getPhone() != null) {
            updatedEmployee.setPhone(utilityService.formatPhoneNumber(updatedEmployee.getPhone()));
        }

        // Validate email if changed
        if (updatedEmployee.getEmail() != null && !utilityService.isValidEmail(updatedEmployee.getEmail())) {
            throw new IllegalArgumentException("Invalid email format: " + updatedEmployee.getEmail());
        }

        return employeeRepository.save(updatedEmployee);
    }

    /**
     * Delete employee
     */
    public boolean deleteEmployee(String id) {
        Objects.requireNonNull(id, "Employee id must not be null");
        if (employeeRepository.existsById(id)) {
            employeeRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Demonstrate PasswordEncoder usage
     * Encode a password for an employee (simulated)
     */
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * Verify a password
     */
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * Generate a random password for new employee
     */
    public String generateTemporaryPassword() {
        return utilityService.generateRandomPassword(12);
    }

    /**
     * Get total employee count
     */
    public long getEmployeeCount() {
        return employeeRepository.count();
    }
}
