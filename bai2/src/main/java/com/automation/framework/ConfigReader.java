package com.automation.framework;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * <h2>ConfigReader - Đọc cấu hình từ config.properties</h2>
 *
 * <p>Singleton class đọc file config.properties từ classpath.
 * Cung cấp các method tiện ích để lấy URL, timeout theo môi trường.</p>
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
                throw new RuntimeException("[ConfigReader] Không tìm thấy file: " + CONFIG_FILE);
            }
            props.load(is);
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
            System.err.println("[ConfigReader] Không tìm thấy key: " + key + " → dùng url.dev");
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
}
