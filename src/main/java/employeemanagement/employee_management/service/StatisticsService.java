package employeemanagement.employee_management.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * StatisticsService - Service for employee statistics with caching
 */
@Service
public class StatisticsService {

    private static final Logger logger = LoggerFactory.getLogger(StatisticsService.class);
    private final EmployeeService employeeService;
    private final DepartmentService departmentService;

    public StatisticsService(EmployeeService employeeService, DepartmentService departmentService) {
        this.employeeService = employeeService;
        this.departmentService = departmentService;
    }

    /**
     * Get employee statistics with 1-minute caching
     * Cache will automatically expire after 1 minute
     */
    @Cacheable(value = "employeeStatistics", key = "'all'")
    public Map<String, Object> getEmployeeStatistics() {
        logger.info("Computing employee statistics (cache miss or expired)");

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalEmployees", employeeService.getEmployeeCount());
        stats.put("totalDepartments", departmentService.getDepartmentCount());

        // Get employee count by department
        Map<String, Long> byDepartment = new HashMap<>();
        departmentService.getAllDepartments().forEach(dept -> {
            long count = employeeService.getEmployeesByDepartment(dept.getId()).size();
            byDepartment.put(dept.getName(), count);
        });
        stats.put("employeesByDepartment", byDepartment);
        stats.put("timestamp", System.currentTimeMillis());

        return stats;
    }
}
