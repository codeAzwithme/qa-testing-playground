package com.restfulbooker.automation.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigManager {

    private static final String CONFIG_FILE = "config.properties";
    private static final Properties PROPERTIES = loadProperties();

    private ConfigManager() {
    }

    public static String baseUri() {
        return get("base.uri");
    }

    public static String username() {
        return get("auth.username");
    }

    public static String password() {
        return get("auth.password");
    }

    public static long maxResponseTimeMillis() {
        return Long.parseLong(get("max.response.time.ms"));
    }

    private static String get(String key) {
        String value = System.getProperty(key, PROPERTIES.getProperty(key));
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing configuration property: " + key);
        }
        return value.trim();
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream stream = ConfigManager.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (stream == null) {
                throw new IllegalStateException(CONFIG_FILE + " not found on the classpath");
            }
            properties.load(stream);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read " + CONFIG_FILE + ": " + exception.getMessage(), exception);
        }
        return properties;
    }
}
