package com.lab9.bai5;

import java.io.InputStream;
import java.util.Properties;

/**
 * ConfigReader - Sử dụng Singleton Pattern.
 * Đọc file config-*.properties dựa theo System.getProperty("env").
 */
public class ConfigReader {
    private static ConfigReader instance;
    private Properties props;

    // Constructure luôn đọc file property 1 lần duy nhất khi class Load
    private ConfigReader() {
        // Mặc định lấy System Property "env". Nếu rỗng hoặc ko tồn tại -> Mặc định
        // 'dev'
        String env = System.getProperty("env");
        if (env == null || env.trim().isEmpty() || env.equals("${env}")) {
            env = "dev";
            System.setProperty("env", "dev"); // Ghi đè fallback để chắc chắn
        }

        String configFile = "config-" + env.toLowerCase() + ".properties";
        props = new Properties();

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(configFile)) {
            if (is == null) {
                throw new RuntimeException(
                        "\n[LỖI] KHÔNG THỂ load file config: " + configFile + " (Bảo đảm gõ đúng -Denv=dev|staging)\n");
            }
            props.load(is);
        } catch (Exception e) {
            throw new RuntimeException("Có lỗi trong lúc xử lý file config properties.", e);
        }
    }

    /**
     * @return Đảm bảo ConfigReader chỉ khỏi tạo đối tượng 1 lần duy nhất trong chu
     *         trình chạy (Singleton).
     */
    public static ConfigReader getInstance() {
        if (instance == null) {
            instance = new ConfigReader();
        }
        return instance;
    }

    // Các hàm Helper lấy Value từ Properties đã load
    public String getBaseUrl() {
        return props.getProperty("url", "https://www.saucedemo.com/");
    }

    public int getExplicitWait() {
        return Integer.parseInt(props.getProperty("explicit.wait", "15"));
    }
}
