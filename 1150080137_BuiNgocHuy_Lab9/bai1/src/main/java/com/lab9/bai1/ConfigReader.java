package com.lab9.bai1;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * <h2>ConfigReader - Đọc cấu hình từ config.properties</h2>
 *
 * <p>
 * Singleton class đọc file {@code config.properties} từ classpath.
 * Cung cấp các method tiện ích để lấy URL, timeout theo môi trường.
 * </p>
 *
 * <h3>Cách dùng:</h3>
 * 
 * <pre>{@code
 * String url = ConfigReader.getBaseUrl("dev");
 * int timeout = ConfigReader.getExplicitTimeout();
 * }</pre>
 *
 * @author Lab9 - Page Object Model Framework
 * @version 1.0
 */
public class ConfigReader {

    private static final String CONFIG_FILE = "config.properties";
    private static Properties props;

    // Khởi tạo 1 lần khi class được load (Singleton)
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

    // Không cho khởi tạo instance
    private ConfigReader() {
    }

    /**
     * Lấy Base URL theo môi trường.
     *
     * @param env tên môi trường: "dev", "staging", "prod"
     * @return URL tương ứng, mặc định dùng "dev" nếu không tìm thấy
     */
    public static String getBaseUrl(String env) {
        String key = "url." + env.toLowerCase().trim();
        String url = props.getProperty(key);
        if (url == null || url.isEmpty()) {
            System.err.println("[ConfigReader] Không tìm thấy key: " + key + " → dùng url.dev");
            url = props.getProperty("url.dev", "https://www.saucedemo.com/");
        }
        return url;
    }

    /**
     * Lấy thời gian Explicit Wait mặc định (giây).
     *
     * @return giá trị timeout, mặc định 15 giây
     */
    public static int getExplicitTimeout() {
        return Integer.parseInt(props.getProperty("timeout.explicit", "15"));
    }

    /**
     * Lấy thời gian chờ tải trang (giây).
     *
     * @return giá trị timeout, mặc định 30 giây
     */
    public static int getPageLoadTimeout() {
        return Integer.parseInt(props.getProperty("timeout.pageload", "30"));
    }

    /**
     * Lấy giá trị bất kỳ từ config theo key.
     *
     * @param key tên property
     * @return giá trị tương ứng, hoặc null nếu không tìm thấy
     */
    public static String get(String key) {
        return props.getProperty(key);
    }
}
