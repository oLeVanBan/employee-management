package employeemanagement.employee_management.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * UtilityService - Service bean for utility functions
 * Demonstrates @Service annotation for creating Spring beans
 */
@Service
public class UtilityService {

    private final AtomicInteger employeeCounter = new AtomicInteger(1000);

    /**
     * Generate automatic employee code
     * Format: EMP-YYYYMMDD-XXXX
     *
     * @return Generated employee code
     */
    public String generateEmployeeCode() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int sequence = employeeCounter.incrementAndGet();
        return String.format("EMP-%s-%04d", datePart, sequence);
    }

    /**
     * Format employee name to proper case
     * Example: "john doe" -> "John Doe"
     *
     * @param name Employee name
     * @return Formatted name
     */
    public String formatEmployeeName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "";
        }

        String[] words = name.trim().toLowerCase().split("\\s+");
        StringBuilder formatted = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                formatted.append(Character.toUpperCase(word.charAt(0)))
                         .append(word.substring(1))
                         .append(" ");
            }
        }

        return formatted.toString().trim();
    }

    /**
     * Format phone number to standard format
     * Example: "0987654321" -> "(098) 765-4321"
     *
     * @param phone Phone number
     * @return Formatted phone number
     */
    public String formatPhoneNumber(String phone) {
        if (phone == null || phone.isEmpty()) {
            return "";
        }

        String cleaned = phone.replaceAll("[^0-9]", "");

        if (cleaned.length() == 10) {
            return String.format("(%s) %s-%s",
                cleaned.substring(0, 3),
                cleaned.substring(3, 6),
                cleaned.substring(6));
        }

        return phone; // Return original if format is unexpected
    }

    /**
     * Validate email format
     *
     * @param email Email address
     * @return true if valid, false otherwise
     */
    public boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    /**
     * Generate a random password
     *
     * @param length Password length
     * @return Random password
     */
    public String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            password.append(chars.charAt(index));
        }

        return password.toString();
    }
}
