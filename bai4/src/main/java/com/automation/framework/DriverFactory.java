package com.automation.framework;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

/**
 * <h2>DriverFactory - Hỗ trợ Selenium Grid và RemoteWebDriver</h2>
 */
public class DriverFactory {

    public static WebDriver createDriver(String browser) {
        // Kiểm tra biến hệ thống grid.url truyền từ Maven (-Dgrid.url=http://localhost:4444)
        String gridUrl = System.getProperty("grid.url");
        if (gridUrl != null && !gridUrl.isBlank()) {
            return createRemoteDriver(browser, gridUrl); // Chạy trên Grid
        }
        return createLocalDriver(browser); // Chạy local bình thường
    }

    private static WebDriver createLocalDriver(String browser) {
        boolean isCI = System.getenv("CI") != null;
        return switch (browser.toLowerCase()) {
            case "firefox" -> createFirefoxDriver(isCI);
            default -> createChromeDriver(isCI);
        };
    }

    private static WebDriver createRemoteDriver(String browser, String gridUrl) {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setBrowserName(browser.toLowerCase());
        
        if (browser.equalsIgnoreCase("chrome")) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--no-sandbox", "--disable-dev-shm-usage");
            caps.merge(options);
        } else if (browser.equalsIgnoreCase("firefox")) {
            FirefoxOptions options = new FirefoxOptions();
            caps.merge(options);
        }
        
        try {
            // Chuẩn hóa Grid URL
            String remoteUrl = gridUrl.endsWith("/") ? gridUrl + "wd/hub" : gridUrl + "/wd/hub";
            RemoteWebDriver driver = new RemoteWebDriver(new URL(remoteUrl), caps);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            return driver;
        } catch (MalformedURLException e) {
            throw new RuntimeException("Grid URL không hợp lệ: " + gridUrl, e);
        }
    }

    private static WebDriver createChromeDriver(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage", "--window-size=1920,1080");
        } else {
            options.addArguments("--start-maximized");
        }
        options.addArguments("--disable-gpu", "--remote-allow-origins=*");
        WebDriverManager.chromedriver().setup();
        return new ChromeDriver(options);
    }

    private static WebDriver createFirefoxDriver(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless) options.addArguments("-headless");
        WebDriverManager.firefoxdriver().setup();
        return new FirefoxDriver(options);
    }
}
