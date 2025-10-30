package config;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * TestConfig.java
 *
 * A centralized configuration utility for reading test environment settings
 * such as base URLs, credentials, form IDs, and output file paths.
 *
 * Usage Example:
 *   String baseUrl = TestConfig.getBaseUrl();
 *   String username = TestConfig.getAdminUsername();
 */
public class TestConfig {

    public static final Properties props = new Properties();

    // Load properties once when class is initialized
    static {
        String configPath = "src/test/resources/test.properties";
        try (InputStream input = new FileInputStream(Paths.get(configPath).toFile())) {
            props.load(input);
            System.out.println("[TestConfig] Loaded configuration from: " + configPath);
        } catch (IOException e) {
            System.err.println("[TestConfig] ERROR: Could not load test.properties file!");
            throw new RuntimeException("Failed to load configuration from " + configPath, e);
        }
    }

    // --- Helper methods to get configuration values ---

    public static String getBaseUrl() {
        return getProperty("base.url");
    }

    public static String getSignupFormUrl() {
        return getProperty("signup.form.url");
    }

    public static String getAdminUsername() {
        return getProperty("admin.username");
    }

    public static String getAdminPassword() {
        return getProperty("admin.password");
    }

    public static String getOutputFile() {
        return getProperty("output.file", "target/email_to_cardId.csv");
    }

    public static String getBrowser() {
        return getProperty("browser", "chrome");
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(getProperty("headless", "false"));
    }

    // Generic property getter (throws if missing)
    private static String getProperty(String key) {
        String value = System.getProperty(key, props.getProperty(key));
        if (value == null || value.trim().isEmpty()) {
            throw new RuntimeException("Missing required property: " + key);
        }
        return value.trim();
    }

    // Generic property getter with default value
    private static String getProperty(String key, String defaultValue) {
        return System.getProperty(key, props.getProperty(key, defaultValue)).trim();
    }

    // Print all loaded configs (useful for debugging)
    public static void printConfigSummary() {
        System.out.println("\n========== Test Configuration ==========");
        props.forEach((k, v) -> System.out.println(k + " = " + v));
        System.out.println("========================================\n");
    }
}

