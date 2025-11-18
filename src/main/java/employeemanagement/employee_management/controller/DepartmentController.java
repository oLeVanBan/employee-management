package employeemanagement.employee_management.controller;

import employeemanagement.employee_management.dto.DepartmentDTO;
import employeemanagement.employee_management.dto.EmployeeDTO;
import employeemanagement.employee_management.mapper.DtoMapper;
import employeemanagement.employee_management.model.Department;
import employeemanagement.employee_management.model.Employee;
import employeemanagement.employee_management.service.DepartmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * DepartmentController - REST API endpoints for Department Management
 */
@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final DtoMapper dtoMapper;

    public DepartmentController(DepartmentService departmentService, DtoMapper dtoMapper) {
        this.departmentService = departmentService;
        this.dtoMapper = dtoMapper;
    }

    /**
     * Create a new department
     * POST /api/departments
     */
    @PostMapping
    public ResponseEntity<DepartmentDTO> createDepartment(@RequestBody Department department) {
        try {
            Department created = departmentService.createDepartment(department);
            return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.toDepartmentDTO(created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get all departments
     * GET /api/departments
     */
    @GetMapping
    public ResponseEntity<List<DepartmentDTO>> getAllDepartments() {
        List<Department> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(dtoMapper.toDepartmentDTOList(departments));
    }

    /**
     * Get department by ID
     * GET /api/departments/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDTO> getDepartmentById(@PathVariable Long id) {
        Optional<Department> department = departmentService.getDepartmentById(id);
        return department.map(d -> ResponseEntity.ok(dtoMapper.toDepartmentDTO(d)))
                        .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update department
     * PUT /api/departments/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<DepartmentDTO> updateDepartment(@PathVariable Long id, @RequestBody Department department) {
        try {
            Department updated = departmentService.updateDepartment(id, department);
            return ResponseEntity.ok(dtoMapper.toDepartmentDTO(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete department
     * DELETE /api/departments/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        boolean deleted = departmentService.deleteDepartment(id);
        return deleted ? ResponseEntity.noContent().build()
                      : ResponseEntity.notFound().build();
    }

    /**
     * Get all employees in a department
     * GET /api/departments/{id}/employees
     */
    @GetMapping("/{id}/employees")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesByDepartment(@PathVariable Long id) {
        try {
            List<Employee> employees = departmentService.getEmployeesByDepartment(id);
            return ResponseEntity.ok(dtoMapper.toEmployeeDTOList(employees));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
