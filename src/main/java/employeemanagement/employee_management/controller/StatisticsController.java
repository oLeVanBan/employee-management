package employeemanagement.employee_management.controller;

import employeemanagement.employee_management.service.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * StatisticsController - REST API endpoints for employee statistics and reporting
 */
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    /**
     * Get basic employee statistics (cached)
     * GET /api/statistics
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getBasicStatistics() {
        return ResponseEntity.ok(statisticsService.getEmployeeStatistics());
    }

    /**
     * Get comprehensive statistics report
     * GET /api/statistics/comprehensive
     */
    @GetMapping("/comprehensive")
    public ResponseEntity<Map<String, Object>> getComprehensiveReport() {
        return ResponseEntity.ok(statisticsService.getComprehensiveReport());
    }

    /**
     * Get employee count by department
     * GET /api/statistics/by-department
     */
    @GetMapping("/by-department")
    public ResponseEntity<Map<String, Long>> getEmployeeCountByDepartment() {
        return ResponseEntity.ok(statisticsService.getEmployeeCountByDepartment());
    }

    /**
     * Get employee count by position
     * GET /api/statistics/by-position
     */
    @GetMapping("/by-position")
    public ResponseEntity<Map<String, Long>> getEmployeeCountByPosition() {
        return ResponseEntity.ok(statisticsService.getEmployeeCountByPosition());
    }


    /**
     * Get departments with employee count
     * GET /api/statistics/departments
     */
    @GetMapping("/departments")
    public ResponseEntity<List<Map<String, Object>>> getDepartmentsWithEmployeeCount() {
        return ResponseEntity.ok(statisticsService.getDepartmentsWithEmployeeCount());
    }
}
