package employeemanagement.employee_management.service;

import employeemanagement.employee_management.dto.DepartmentStatistics;
import employeemanagement.employee_management.repository.DepartmentRepository;
import employeemanagement.employee_management.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * StatisticsService - Service for employee statistics with caching
 */
@Service
public class StatisticsService {

    private static final Logger logger = LoggerFactory.getLogger(StatisticsService.class);
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public StatisticsService(EmployeeRepository employeeRepository,
                           DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    /**
     * Get employee statistics with 1-minute caching
     * Cache will automatically expire after 1 minute
     */
    @Cacheable(value = "employeeStatistics", key = "'all'")
    public Map<String, Object> getEmployeeStatistics() {
        logger.info("Computing employee statistics (cache miss or expired)");

        Map<String, Object> stats = new HashMap<>();

        // Total counts using @Query methods
        stats.put("totalEmployees", employeeRepository.countTotalEmployees());
        stats.put("totalDepartments", departmentRepository.countTotalDepartments());

        // Employee count by department using @Query
        Map<String, Long> byDepartment = new LinkedHashMap<>();
        List<Object[]> departmentStats = employeeRepository.countEmployeesByDepartment();
        for (Object[] row : departmentStats) {
            String deptName = (String) row[0];
            Long count = (Long) row[1];
            byDepartment.put(deptName, count);
        }
        stats.put("employeesByDepartment", byDepartment);

        stats.put("timestamp", System.currentTimeMillis());

        return stats;
    }

    /**
     * Get employee count by department (detailed)
     */
    public Map<String, Long> getEmployeeCountByDepartment() {
        logger.info("Getting employee count by department");

        Map<String, Long> result = new LinkedHashMap<>();
        List<Object[]> data = employeeRepository.countEmployeesByDepartment();

        for (Object[] row : data) {
            String departmentName = (String) row[0];
            Long count = (Long) row[1];
            result.put(departmentName, count);
        }

        return result;
    }

    /**
     * Get employee count by position
     */
    public Map<String, Long> getEmployeeCountByPosition() {
        logger.info("Getting employee count by position");

        Map<String, Long> result = new LinkedHashMap<>();
        List<Object[]> data = employeeRepository.countEmployeesByPosition();

        for (Object[] row : data) {
            String position = (String) row[0];
            Long count = (Long) row[1];
            result.put(position, count);
        }

        return result;
    }


    /**
     * Get departments with employee count
     */
    public List<Map<String, Object>> getDepartmentsWithEmployeeCount() {
        logger.info("Getting departments with employee count");

        List<DepartmentStatistics> data = departmentRepository.getDepartmentsWithEmployeeCount();

        return data.stream().map(stat -> {
            Map<String, Object> dept = new HashMap<>();
            dept.put("departmentName", stat.getDepartmentName());
            dept.put("employeeCount", stat.getEmployeeCount());
            return dept;
        }).collect(Collectors.toList());
    }

    /**
     * Get comprehensive statistics report
     */
    public Map<String, Object> getComprehensiveReport() {
        logger.info("Generating comprehensive statistics report");

        Map<String, Object> report = new HashMap<>();
        report.put("totalEmployees", employeeRepository.countTotalEmployees());
        report.put("totalDepartments", departmentRepository.countTotalDepartments());
        report.put("employeesByDepartment", getEmployeeCountByDepartment());
        report.put("employeesByPosition", getEmployeeCountByPosition());
        report.put("departmentDetails", getDepartmentsWithEmployeeCount());
        report.put("generatedAt", System.currentTimeMillis());

        return report;
    }
}
