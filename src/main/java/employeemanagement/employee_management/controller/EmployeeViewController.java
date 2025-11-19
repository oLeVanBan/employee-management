package employeemanagement.employee_management.controller;

import employeemanagement.employee_management.dto.DepartmentDTO;
import employeemanagement.employee_management.dto.EmployeeDTO;
import employeemanagement.employee_management.dto.EmployeeForm;
import employeemanagement.employee_management.exception.ValidationException;
import employeemanagement.employee_management.mapper.DtoMapper;
import employeemanagement.employee_management.model.Department;
import employeemanagement.employee_management.model.Employee;
import employeemanagement.employee_management.service.DepartmentService;
import employeemanagement.employee_management.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Objects;

/**
 * MVC controller rendering Thymeleaf pages for basic employee management flows.
 */
@Controller
@RequestMapping("/employees")
public class EmployeeViewController {

    private final EmployeeService employeeService;
    private final DepartmentService departmentService;
    private final DtoMapper dtoMapper;

    public EmployeeViewController(EmployeeService employeeService,
                                  DepartmentService departmentService,
                                  DtoMapper dtoMapper) {
        this.employeeService = employeeService;
        this.departmentService = departmentService;
        this.dtoMapper = dtoMapper;
    }

    @GetMapping
    public String showEmployees(@RequestParam(value = "name", required = false) String name,
                                @RequestParam(value = "department", required = false) String departmentName,
                                Model model) {
        List<EmployeeDTO> employees = dtoMapper.toEmployeeDTOList(
                employeeService.searchEmployees(name, departmentName)
        );
        model.addAttribute("employees", employees);
        model.addAttribute("searchName", name);
        model.addAttribute("searchDepartment", departmentName);
        model.addAttribute("departments", getDepartmentOptions());
        return "employees/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("employeeForm")) {
            model.addAttribute("employeeForm", new EmployeeForm());
        }
        model.addAttribute("departments", getDepartmentOptions());
        return "employees/add";
    }

    @PostMapping
    public String createEmployee(@Valid @ModelAttribute("employeeForm") EmployeeForm form,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("departments", getDepartmentOptions());
            return "employees/add";
        }

        Employee employee = mapFormToEmployee(form);
        try {
            employeeService.createEmployee(employee);
        } catch (ValidationException validationException) {
            applyServiceErrors(validationException, bindingResult);
            model.addAttribute("departments", getDepartmentOptions());
            return "employees/add";
        } catch (IllegalArgumentException illegalArgumentException) {
            String message = illegalArgumentException.getMessage() == null
                    ? "Unexpected error"
                    : illegalArgumentException.getMessage();
            bindingResult.reject("illegalArgument", Objects.requireNonNull(message));
            model.addAttribute("departments", getDepartmentOptions());
            return "employees/add";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Employee created successfully");
        return "redirect:/employees";
    }

    private void applyServiceErrors(ValidationException validationException, BindingResult bindingResult) {
        List<String> errors = validationException.getErrors();
        if (errors == null || errors.isEmpty()) {
            String message = validationException.getMessage() == null
                    ? "Validation failed"
                    : validationException.getMessage();
            bindingResult.reject("validationError", Objects.requireNonNull(message));
            return;
        }

        for (String error : errors) {
            int delimiterIndex = error.indexOf(":");
            if (delimiterIndex > 0) {
                String field = error.substring(0, delimiterIndex).trim();
                String message = error.substring(delimiterIndex + 1).trim();
                bindingResult.rejectValue(field, "validationError", Objects.requireNonNull(message));
            } else {
                bindingResult.reject("validationError", error);
            }
        }
    }

    private Employee mapFormToEmployee(EmployeeForm form) {
        Employee employee = new Employee();
        employee.setId(form.getId());
        employee.setName(form.getName());
        employee.setEmail(form.getEmail());
        employee.setPhone(form.getPhone());
        employee.setPosition(form.getPosition());
        if (form.getDepartmentId() != null) {
            Department department = new Department();
            department.setId(form.getDepartmentId());
            employee.setDepartment(department);
        }
        return employee;
    }

    private List<DepartmentDTO> getDepartmentOptions() {
        return dtoMapper.toDepartmentDTOList(departmentService.getAllDepartments());
    }
}