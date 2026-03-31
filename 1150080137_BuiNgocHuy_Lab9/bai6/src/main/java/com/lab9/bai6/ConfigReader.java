package com.lab9.bai6;

import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static final String CONFIG_FILE = "config.properties";
    private static Properties props = new Properties();

    static {
        try (InputStream is = ConfigReader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (is != null) {
                props.load(is);
            } else {
                System.err.println("Không tìm thấy file config.properties, sẽ dùng giá trị mặc định cho Max Retry");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return Số lượng retry. Mặc định là 0 nếu không cấu hình file config.
     */
    public static int getMaxRetryCount() {
        String retryStr = props.getProperty("retry.count", "0");
        try {
            return Integer.parseInt(retryStr);
        } catch (NumberFormatException e) {
            return 0; // Fallback
        }
    }
}
