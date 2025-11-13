package employeemanagement.employee_management.dto;

/**
 * EmployeeDTO - Data Transfer Object for Employee
 * Demonstrates ModelMapper usage for object mapping
 */
public class EmployeeDTO {
    private String id;
    private String fullName;
    private String email;
    private String contactNumber;
    private String dept;
    private String role;

    public EmployeeDTO() {
    }

    public EmployeeDTO(String id, String fullName, String email, String contactNumber, String dept, String role) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.contactNumber = contactNumber;
        this.dept = dept;
        this.role = role;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
