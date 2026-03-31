package com.automation.framework;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * <h2>ConfigReader - Đọc cấu hình từ config.properties hoặc Environment</h2>
 *
 * <p>Cung cấp các method tiện ích để lấy URL, timeout, và đặc biệt là
 * thông tin đăng nhập tự động fallback từ Biến Môi Trường (Environments)
 * về file config.</p>
 *
 * @author Lab11 - CI/CD Framework
 * @version 1.0
 */
public class ConfigReader {

    private static final String CONFIG_FILE = "config.properties";
    private static Properties props;

    static {
        props = new Properties();
        try (InputStream is = ConfigReader.class
                .getClassLoader()
                .getResourceAsStream(CONFIG_FILE)) {
            if (is == null) {
                System.err.println("[ConfigReader] Cảnh báo: Không tìm thấy file " + CONFIG_FILE);
            } else {
                props.load(is);
            }
        } catch (IOException e) {
            throw new RuntimeException("[ConfigReader] Lỗi đọc file config: " + e.getMessage(), e);
        }
    }

    private ConfigReader() {
    }

    public static String getBaseUrl(String env) {
        String key = "url." + env.toLowerCase().trim();
        String url = props.getProperty(key);
        if (url == null || url.isEmpty()) {
            url = props.getProperty("url.dev", "https://www.saucedemo.com/");
        }
        return url;
    }

    public static int getExplicitTimeout() {
        return Integer.parseInt(props.getProperty("timeout.explicit", "15"));
    }

    public static int getPageLoadTimeout() {
        return Integer.parseInt(props.getProperty("timeout.pageload", "30"));
    }

    public static String get(String key) {
        return props.getProperty(key);
    }

    /**
     * Ưu tiên đọc từ biến môi trường (khi chạy trên CI/CD)
     * Fallback: đọc từ file config (khi chạy local)
     */
    public static String getAppUsername() {
        String username = System.getenv("APP_USERNAME");
        if (username == null || username.isBlank()) {
            username = props.getProperty("app.username");
        }
        return username;
    }

    /**
     * Ưu tiên đọc từ biến môi trường (khi chạy trên CI/CD)
     * Fallback: đọc từ file config (khi chạy local)
     */
    public static String getAppPassword() {
        String password = System.getenv("APP_PASSWORD");
        if (password == null || password.isBlank()) {
            password = props.getProperty("app.password");
        }
        return password;
    }
}
