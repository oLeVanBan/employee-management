package employeemanagement.employee_management.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

/**
 * AppConfig - Configuration class for defining custom beans
 * Demonstrates @Configuration and @Bean annotations
 */
@Configuration
public class AppConfig {

    /**
     * Define PasswordEncoder bean using BCrypt algorithm
     * This bean will be managed by Spring IoC Container
     *
     * @return BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt is a strong hashing algorithm for passwords
        // It automatically handles salting and is resistant to brute force attacks
        return new BCryptPasswordEncoder();
    }

    /**
     * Define ModelMapper bean for object mapping
     * Useful for converting between DTOs and Entities
     *
     * @return ModelMapper instance with custom configuration
     */
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Configure ModelMapper settings
        modelMapper.getConfiguration()
            .setMatchingStrategy(MatchingStrategies.STRICT) // Use strict matching
            .setSkipNullEnabled(true) // Skip null values during mapping
            .setAmbiguityIgnored(true); // Ignore ambiguous mappings

        return modelMapper;
    }

    /**
     * Custom bean for application metadata
     *
     * @return AppMetadata instance
     */
    @Bean
    public AppMetadata appMetadata() {
        return new AppMetadata(
            "Employee Management System",
            "1.0.0",
            "Demo application for Spring Boot IoC and DI"
        );
    }

    /**
     * Configure CacheManager with simple in-memory cache
     * Cache entries will expire after 1 minute (managed by @Cacheable ttl or eviction policy)
     *
     * @return CacheManager instance
     */
    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(
            new ConcurrentMapCache("employeeStatistics")
        ));
        cacheManager.initializeCaches();
        return cacheManager;
    }

    /**
     * Inner class to demonstrate custom bean creation
     */
    public static class AppMetadata {
        private final String appName;
        private final String version;
        private final String description;

        public AppMetadata(String appName, String version, String description) {
            this.appName = appName;
            this.version = version;
            this.description = description;
        }

        public String getAppName() {
            return appName;
        }

        public String getVersion() {
            return version;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return String.format("%s v%s - %s", appName, version, description);
        }
    }
}
