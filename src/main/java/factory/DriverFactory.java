package factory;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

// CẢI TIẾN: Sử dụng ThreadLocal để WebDriver thread-safe, là bước đi trước rất tốt cho Bài 2 và Bài 4 (Parallel Test)
public class DriverFactory {
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    public static void createDriver(String browser) {
        boolean isCI = System.getenv("CI") != null; // GitHub Actions tự set biến CI=true
        WebDriver driver = switch (browser.toLowerCase()) {
            case "firefox" -> createFirefoxDriver(isCI);
            default -> createChromeDriver(isCI);
        };
        driverThreadLocal.set(driver);
    }

    public static void quitDriver() {
        if (driverThreadLocal.get() != null) {
            driverThreadLocal.get().quit();
            driverThreadLocal.remove();     // Dọn dẹp mem
        }
    }

    private static WebDriver createChromeDriver(boolean headless) {
        ChromeOptions options = new ChromeOptions();

        // CẢI TIẾN 1: Thêm PageLoadStrategy là NORMAL hoặc EAGER để test ổn định và nhanh hơn
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        
        // CẢI TIẾN 2: Tắt các popup, banner phiền phức ngăn cản automation click
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-extensions");

        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox"); // Bắt buộc cho môi trường CI Linux
            options.addArguments("--disable-dev-shm-usage"); // Khắc phục lỗi Out Of Memory
            options.addArguments("--window-size=1920,1080");
        } else {
            options.addArguments("--start-maximized");
        }
        
        // CẢI TIẾN 3: Bắt đầu từ Selenium 4.6+, Selenium Manager được tích hợp sẵn
        // nên chúng ta có thể BỎ CÂU LỆNH WebDriverManager.chromedriver().setup() đi!
        return new ChromeDriver(options);
    }

    private static WebDriver createFirefoxDriver(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();

        if (headless) {
            options.addArguments("-headless");
            // CẢI TIẾN 4: Gán độ phân giải tĩnh cho firefox headless để tránh element bóp méo
            options.addArguments("--width=1920");
            options.addArguments("--height=1080");
        }
        
        return new FirefoxDriver(options);
    }
}
