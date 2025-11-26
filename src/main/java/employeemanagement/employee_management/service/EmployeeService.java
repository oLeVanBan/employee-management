package employeemanagement.employee_management.service;

import employeemanagement.employee_management.exception.ResourceNotFoundException;
import employeemanagement.employee_management.exception.ValidationException;
import employeemanagement.employee_management.model.Employee;
import employeemanagement.employee_management.model.Department;
import employeemanagement.employee_management.repository.EmployeeRepository;
import employeemanagement.employee_management.repository.DepartmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

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
    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    /**
     * Constructor Injection - Recommended approach for DI
     * Spring will automatically inject all required beans
     * No need for @Autowired annotation on constructor (since Spring 4.3)
     */
    public EmployeeService(
            EmployeeRepository employeeRepository,
            DepartmentRepository departmentRepository,
            UtilityService utilityService,
            PasswordEncoder passwordEncoder
    ) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.utilityService = utilityService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Create a new employee with auto-generated ID and formatted data
     *
     * @param employee Employee data
     * @return Created employee
     */
    public Employee createEmployee(Employee employee) {
        employee.setDepartment(resolveDepartment(employee.getDepartment()));

        // If client provided an ID, validate uniqueness; otherwise generate one
        String providedId = employee.getId();
        if (providedId != null && !providedId.trim().isEmpty()) {
            if (employeeRepository.existsById(providedId)) {
                throw new ValidationException("Validation failed", java.util.Collections.singletonList("id: Employee id '" + providedId + "' already exists"));
            }
        } else {
            // Generate employee code using UtilityService
            String employeeCode = utilityService.generateEmployeeCode();
            employee.setId(employeeCode);
        }

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
        if (employee.getEmail() != null && employeeRepository.existsByEmail(employee.getEmail())) {
            throw new ValidationException("Validation failed", java.util.Collections.singletonList("email: Employee email '" + employee.getEmail() + "' already exists"));
        }

        Employee savedEmployee = Objects.requireNonNull(
            employeeRepository.save(employee),
            "Saved employee must not be null"
        );
        logger.info("Created employee with id={} and email={}", savedEmployee.getId(), savedEmployee.getEmail());
        return savedEmployee;
    }

    public Employee getEmployeeOrThrow(String id) {
        Objects.requireNonNull(id, "Employee id must not be null");
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
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
            .orElseThrow(() -> new ResourceNotFoundException("Department", "name", departmentName));
        return employeeRepository.findByDepartment(department);
    }

    /**
     * Update employee
     */
    public Employee updateEmployee(String id, Employee updatedEmployee) {
        Objects.requireNonNull(id, "Employee id must not be null");
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        if (updatedEmployee.getName() != null) {
            employee.setName(utilityService.formatEmployeeName(updatedEmployee.getName()));
        }

        if (updatedEmployee.getPhone() != null) {
            employee.setPhone(utilityService.formatPhoneNumber(updatedEmployee.getPhone()));
        }

        if (updatedEmployee.getEmail() != null) {
            if (!utilityService.isValidEmail(updatedEmployee.getEmail())) {
                throw new IllegalArgumentException("Invalid email format: " + updatedEmployee.getEmail());
            }
            employee.setEmail(updatedEmployee.getEmail());
        }

        if (updatedEmployee.getPosition() != null) {
            employee.setPosition(updatedEmployee.getPosition());
        }

        if (updatedEmployee.getDepartment() != null) {
            employee.setDepartment(resolveDepartment(updatedEmployee.getDepartment()));
        }

        Employee saved = Objects.requireNonNull(
            employeeRepository.save(employee),
            "Saved employee must not be null"
        );
        logger.info("Updated employee with id={}", saved.getId());
        return saved;
    }

    /**
     * Delete employee
     */
    public void deleteEmployee(String id) {
        Objects.requireNonNull(id, "Employee id must not be null");
        Employee employee = Objects.requireNonNull(getEmployeeOrThrow(id));
        employeeRepository.delete(employee);
        logger.info("Deleted employee with id={}", id);
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

    private Department resolveDepartment(Department department) {
        if (department == null || department.getId() == null) {
            return null;
        }

        Long departmentId = Objects.requireNonNull(department.getId(), "Department id must not be null");
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", departmentId));
    }
}
