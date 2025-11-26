package employeemanagement.employee_management.config;

import employeemanagement.employee_management.model.Department;
import employeemanagement.employee_management.model.Employee;
import employeemanagement.employee_management.model.User;
import employeemanagement.employee_management.repository.DepartmentRepository;
import employeemanagement.employee_management.repository.EmployeeRepository;
import employeemanagement.employee_management.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * DataInitializer - Initialize sample data on application startup
 */
@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(DepartmentRepository departmentRepository,
                                     EmployeeRepository employeeRepository,
                                     UserRepository userRepository,
                                     PasswordEncoder passwordEncoder) {
        return args -> {
            // Check if data already exists
            if (departmentRepository.count() == 0) {
                System.out.println("Initializing sample data...");

                // Create Departments
                Department itDept = new Department("IT", "Information Technology Department");
                Department hrDept = new Department("HR", "Human Resources Department");
                Department financeDept = new Department("Finance", "Finance Department");
                Department marketingDept = new Department("Marketing", "Marketing Department");

                departmentRepository.save(itDept);
                departmentRepository.save(hrDept);
                departmentRepository.save(financeDept);
                departmentRepository.save(marketingDept);

                System.out.println("✅ Sample departments created!");

                // Create Sample Employees
                Employee emp1 = new Employee();
                emp1.setId("EMP-20251113-0001");
                emp1.setName("Nguyen Van A");
                emp1.setEmail("nguyenvana@example.com");
                emp1.setPhone("0912345678");
                emp1.setDepartment(itDept);
                emp1.setPosition("Senior Developer");

                Employee emp2 = new Employee();
                emp2.setId("EMP-20251113-0002");
                emp2.setName("Tran Thi B");
                emp2.setEmail("tranthib@example.com");
                emp2.setPhone("0987654321");
                emp2.setDepartment(hrDept);
                emp2.setPosition("HR Manager");

                Employee emp3 = new Employee();
                emp3.setId("EMP-20251113-0003");
                emp3.setName("Le Van C");
                emp3.setEmail("levanc@example.com");
                emp3.setPhone("0909123456");
                emp3.setDepartment(itDept);
                emp3.setPosition("DevOps Engineer");

                employeeRepository.save(emp1);
                employeeRepository.save(emp2);
                employeeRepository.save(emp3);

                System.out.println("✅ Sample employees created!");
                System.out.println("📊 Total Departments: " + departmentRepository.count());
                System.out.println("📊 Total Employees: " + employeeRepository.count());
            } else {
                System.out.println("📊 Data already exists. Skipping initialization.");
                System.out.println("📊 Total Departments: " + departmentRepository.count());
                System.out.println("📊 Total Employees: " + employeeRepository.count());
            }
            if (userRepository.count() == 0) {
                // Create default admin user
                User adminUser = new User();
                adminUser.setUsername("admin");
                adminUser.setPassword(passwordEncoder.encode("admin123")); // Hash password
                adminUser.addRole("ADMIN");
                userRepository.save(adminUser);
                System.out.println("✅ Default admin user created!");

                // Create regular user
                User regularUser = new User();
                regularUser.setUsername("user");
                regularUser.setPassword(passwordEncoder.encode("user123")); // Hash password
                regularUser.addRole("USER");
                userRepository.save(regularUser);
                System.out.println("✅ Default regular user created!");
            } else {
                System.out.println("📊 Users already exist. Skipping admin user creation.");
            }
        };
    }
}
