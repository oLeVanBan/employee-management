package employeemanagement.employee_management.controller;

import employeemanagement.employee_management.config.AppConfig.AppMetadata;
import employeemanagement.employee_management.dto.EmployeeDTO;
import employeemanagement.employee_management.exception.ValidationException;
import employeemanagement.employee_management.mapper.DtoMapper;
import employeemanagement.employee_management.model.Employee;
import employeemanagement.employee_management.service.EmployeeService;
import employeemanagement.employee_management.service.UtilityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EmployeeController - REST API endpoints for Employee Management
 * Demonstrates different types of Dependency Injection
 */
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    // Constructor Injection - Recommended approach
    private final EmployeeService employeeService;
    private final UtilityService utilityService;
    private final DtoMapper dtoMapper;

    // Field Injection using @Autowired - Less preferred but shown for demonstration
    @Autowired
    private AppMetadata appMetadata;

    /**
     * Constructor Injection
     * Spring automatically injects the required beans
     */
    public EmployeeController(EmployeeService employeeService, UtilityService utilityService, DtoMapper dtoMapper) {
        this.employeeService = employeeService;
        this.utilityService = utilityService;
        this.dtoMapper = dtoMapper;
    }

    /**
     * Get application metadata
     * Demonstrates using a custom bean defined in @Configuration
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> getAppInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("application", appMetadata.getAppName());
        info.put("version", appMetadata.getVersion());
        info.put("description", appMetadata.getDescription());
        info.put("totalEmployees", String.valueOf(employeeService.getEmployeeCount()));
        return ResponseEntity.ok(info);
    }

    /**
     * Create a new employee
     * POST /api/employees
     */
    @PostMapping
    public ResponseEntity<EmployeeDTO> createEmployee(@Valid @RequestBody Employee employee,
                                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw ValidationException.fromBindingResult(bindingResult);
        }

        Employee created = employeeService.createEmployee(employee);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.toEmployeeDTO(created));
    }

    /**
     * Get employees with optional search filters
     * GET /api/employees?name=John&departmentName=IT
     */
    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "departmentName", required = false) String departmentName
    ) {
        List<Employee> employees = employeeService.searchEmployees(name, departmentName);
        return ResponseEntity.ok(dtoMapper.toEmployeeDTOList(employees));
    }

    /**
     * Get employee by ID
     * GET /api/employees/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable String id) {
        Employee employee = employeeService.getEmployeeOrThrow(null);
        return ResponseEntity.ok(dtoMapper.toEmployeeDTO(employee));
    }

    /**
     * Update employee
     * PUT /api/employees/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDTO> updateEmployee(@PathVariable String id,
                                                      @Valid @RequestBody Employee employee,
                                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw ValidationException.fromBindingResult(bindingResult);
        }

        Employee updated = employeeService.updateEmployee(id, employee);
        return ResponseEntity.ok(dtoMapper.toEmployeeDTO(updated));
    }

    /**
     * Delete employee
     * DELETE /api/employees/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable String id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Demo endpoint: Generate employee code
     * GET /api/employees/demo/generate-code
     */
    @GetMapping("/demo/generate-code")
    public ResponseEntity<Map<String, String>> generateEmployeeCode() {
        Map<String, String> response = new HashMap<>();
        response.put("employeeCode", utilityService.generateEmployeeCode());
        return ResponseEntity.ok(response);
    }

    /**
     * Demo endpoint: Format name
     * GET /api/employees/demo/format-name?name=john doe
     */
    @GetMapping("/demo/format-name")
    public ResponseEntity<Map<String, String>> formatName(@RequestParam String name) {
        Map<String, String> response = new HashMap<>();
        response.put("original", name);
        response.put("formatted", utilityService.formatEmployeeName(name));
        return ResponseEntity.ok(response);
    }

    /**
     * Demo endpoint: Generate temporary password
     * GET /api/employees/demo/generate-password
     */
    @GetMapping("/demo/generate-password")
    public ResponseEntity<Map<String, String>> generatePassword() {
        String rawPassword = employeeService.generateTemporaryPassword();
        String encodedPassword = employeeService.encodePassword(rawPassword);

        Map<String, String> response = new HashMap<>();
        response.put("temporaryPassword", rawPassword);
        response.put("encodedPassword", encodedPassword);
        response.put("note", "This demonstrates PasswordEncoder bean from @Configuration");

        return ResponseEntity.ok(response);
    }

    /**
     * Demo endpoint: Validate email
     * GET /api/employees/demo/validate-email?email=test@example.com
     */
    @GetMapping("/demo/validate-email")
    public ResponseEntity<Map<String, Object>> validateEmail(@RequestParam String email) {
        Map<String, Object> response = new HashMap<>();
        response.put("email", email);
        response.put("isValid", utilityService.isValidEmail(email));
        return ResponseEntity.ok(response);
    }
}
