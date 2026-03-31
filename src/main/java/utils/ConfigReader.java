package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ConfigReader - Đọc cấu hình từ config.properties (Singleton).
 */
public class ConfigReader {

    private static final String CONFIG_FILE = "config.properties";
    private static final Properties props;

    static {
        props = new Properties();
        try (InputStream is = ConfigReader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (is == null)
                throw new RuntimeException("[ConfigReader] Không tìm thấy: " + CONFIG_FILE);
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException("[ConfigReader] Lỗi đọc config: " + e.getMessage(), e);
        }
    }

    private ConfigReader() {
    }

    public static String getBaseUrl(String env) {
        String key = "url." + env.toLowerCase().trim();
        String url = props.getProperty(key);
        if (url == null || url.isEmpty())
            url = props.getProperty("url.dev", "https://www.saucedemo.com/");
        return url;
    }

    public static int getExplicitTimeout() {
        return Integer.parseInt(props.getProperty("timeout.explicit", "15"));
    }

    public static String get(String key) {
        return props.getProperty(key);
    }
}
