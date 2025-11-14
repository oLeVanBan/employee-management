package employeemanagement.employee_management.mapper;

import employeemanagement.employee_management.dto.DepartmentDTO;
import employeemanagement.employee_management.dto.EmployeeDTO;
import employeemanagement.employee_management.model.Department;
import employeemanagement.employee_management.model.Employee;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Centralized mapper for converting entities into DTOs used by controllers.
 */
@Component
public class DtoMapper {

    public EmployeeDTO toEmployeeDTO(Employee employee) {
        if (employee == null) {
            return null;
        }

        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(employee.getId());
        dto.setFullName(employee.getName());
        dto.setEmail(employee.getEmail());
        dto.setContactNumber(employee.getPhone());
        dto.setRole(employee.getPosition());
        dto.setDepartment(toDepartmentDTO(employee.getDepartment()));
        return dto;
    }

    public List<EmployeeDTO> toEmployeeDTOList(List<Employee> employees) {
        if (employees == null || employees.isEmpty()) {
            return Collections.emptyList();
        }
        return employees.stream()
                .map(this::toEmployeeDTO)
                .collect(Collectors.toList());
    }

    public DepartmentDTO toDepartmentDTO(Department department) {
        if (department == null) {
            return null;
        }

        DepartmentDTO dto = new DepartmentDTO();
        dto.setId(department.getId());
        dto.setName(department.getName());
        dto.setDescription(department.getDescription());
        return dto;
    }

    public List<DepartmentDTO> toDepartmentDTOList(List<Department> departments) {
        if (departments == null || departments.isEmpty()) {
            return Collections.emptyList();
        }
        return departments.stream()
                .map(this::toDepartmentDTO)
                .collect(Collectors.toList());
    }
}
