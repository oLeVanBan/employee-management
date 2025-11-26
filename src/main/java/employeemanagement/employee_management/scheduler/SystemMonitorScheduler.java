package employeemanagement.employee_management.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * SystemMonitorScheduler - Scheduled tasks for system monitoring
 * Demonstrates @Scheduled annotation usage
 */
@Component
public class SystemMonitorScheduler {

    private static final Logger logger = LoggerFactory.getLogger(SystemMonitorScheduler.class);

    /**
     * Log system status every 30 seconds
     * fixedRate = 30000ms = 30 seconds
     */
    @Scheduled(fixedRate = 30000)
    public void logSystemStatus() {
        logger.info("System running...");
    }
}
