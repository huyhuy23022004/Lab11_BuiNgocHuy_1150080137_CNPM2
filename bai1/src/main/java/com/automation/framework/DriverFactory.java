package com.automation.framework;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 * <h2>DriverFactory - Tạo WebDriver với hỗ trợ Headless cho CI/CD</h2>
 *
 * <p>Tự động phát hiện môi trường CI (GitHub Actions đặt biến CI=true)
 * và bật chế độ headless khi chạy trên CI server.</p>
 *
 * <p><b>Lý do cần headless:</b> CI server (Ubuntu runner) không có màn hình thật,
 * nếu không bật headless sẽ báo lỗi "cannot open display".</p>
 *
 * @author Lab11 - GitHub Actions CI/CD
 * @version 1.0
 */
public class DriverFactory {

    /**
     * Tạo WebDriver theo loại browser.
     * Tự động bật headless nếu đang chạy trên CI.
     *
     * @param browser loại trình duyệt: "chrome" hoặc "firefox"
     * @return WebDriver instance đã cấu hình
     */
    public static WebDriver createDriver(String browser) {
        // GitHub Actions tự đặt biến CI=true
        boolean isCI = System.getenv("CI") != null;
        return switch (browser.toLowerCase()) {
            case "firefox" -> createFirefoxDriver(isCI);
            default -> createChromeDriver(isCI);
        };
    }

    /**
     * Tạo ChromeDriver với các options phù hợp cho CI.
     *
     * @param headless true nếu đang chạy trên CI
     * @return ChromeDriver instance
     */
    private static WebDriver createChromeDriver(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless=new"); // Chrome 112+
            options.addArguments("--no-sandbox");   // Bắt buộc trên Linux CI
            options.addArguments("--disable-dev-shm-usage"); // Tránh lỗi OOM
            options.addArguments("--window-size=1920,1080");
        } else {
            options.addArguments("--start-maximized");
        }
        // Thêm options để ổn định
        options.addArguments("--disable-gpu");
        options.addArguments("--remote-allow-origins=*");
        WebDriverManager.chromedriver().setup();
        return new ChromeDriver(options);
    }

    /**
     * Tạo FirefoxDriver với headless nếu cần.
     *
     * @param headless true nếu đang chạy trên CI
     * @return FirefoxDriver instance
     */
    private static WebDriver createFirefoxDriver(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless) options.addArguments("-headless");
        WebDriverManager.firefoxdriver().setup();
        return new FirefoxDriver(options);
    }
}
