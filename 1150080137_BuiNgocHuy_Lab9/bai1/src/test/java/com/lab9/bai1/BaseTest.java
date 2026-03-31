package com.lab9.bai1;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <h2>BaseTest - Lớp nền tảng cho tất cả Test Class</h2>
 *
 * <p>
 * Mọi Test Class đều kế thừa từ BaseTest để tự động khởi tạo/đóng WebDriver
 * và chụp ảnh màn hình khi test fail.
 * </p>
 *
 * <h3>Các tính năng chính:</h3>
 * <ul>
 * <li><b>ThreadLocal&lt;WebDriver&gt;</b>: Mỗi thread có driver riêng →
 * hỗ trợ chạy song song parallel="methods" không xung đột</li>
 * <li><b>@BeforeMethod</b>: Nhận tham số browser và env từ testng.xml
 * qua {@code @Parameters} và {@code @Optional}</li>
 * <li><b>@AfterMethod</b>: Tự động chụp ảnh màn hình khi test FAIL,
 * lưu vào target/screenshots/</li>
 * <li><b>Không dùng biến static</b>: Driver được quản lý qua ThreadLocal</li>
 * </ul>
 *
 * <h3>Cách dùng trong testng.xml:</h3>
 * 
 * <pre>{@code
 * <parameter name="browser" value="chrome"/>
 * <parameter name="env" value="staging"/>
 * }</pre>
 *
 * @author Lab9 - Page Object Model Framework
 * @version 1.0
 */
public class BaseTest {

    /**
     * ThreadLocal chứa WebDriver - mỗi thread có instance riêng biệt.
     *
     * <p>
     * <b>Tại sao dùng ThreadLocal:</b>
     * </p>
     * <ul>
     * <li>Khi chạy parallel, mỗi test method chạy trên một thread khác nhau</li>
     * <li>Nếu dùng biến static/instance thông thường → các thread dùng chung
     * driver → race condition → crash</li>
     * <li>ThreadLocal đảm bảo mỗi thread đọc/ghi driver của chính nó</li>
     * </ul>
     */
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    /** Định dạng timestamp cho tên file screenshot */
    private static final String SCREENSHOT_TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss_SSS";

    /** Thư mục lưu screenshot (tương đối từ project root) */
    private static final String SCREENSHOT_DIR = "target/screenshots";

    // =========================================================
    // GETTER - truy cập driver từ subclass hoặc method khác
    // =========================================================

    /**
     * Lấy WebDriver của thread hiện tại.
     *
     * <p>
     * Được dùng trong Page Object khi cần truy cập driver từ test method.
     * </p>
     *
     * @return WebDriver instance của thread hiện tại
     */
    public WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    // =========================================================
    // @BeforeMethod - Khởi tạo WebDriver trước mỗi test
    // =========================================================

    /**
     * Khởi tạo WebDriver trước mỗi test method.
     *
     * <p>
     * <b>Tham số từ testng.xml (dùng @Parameters + @Optional):</b>
     * </p>
     * <ul>
     * <li>{@code browser}: loại trình duyệt — "chrome" (mặc định) hoặc
     * "firefox"</li>
     * <li>{@code env}: môi trường — "dev" (mặc định), "staging", "prod"</li>
     * </ul>
     *
     * <p>
     * {@code @Optional} cho phép test chạy được ngay cả khi testng.xml
     * không khai báo parameter, sẽ dùng giá trị mặc định.
     * </p>
     *
     * @param browser tên trình duyệt ("chrome" hoặc "firefox"), mặc định "chrome"
     * @param env     tên môi trường ("dev", "staging", "prod"), mặc định "dev"
     */
    @BeforeMethod
    @Parameters({ "browser", "env" })
    public void setUp(
            @Optional("chrome") String browser,
            @Optional("dev") String env) {
        System.out.println("══════════════════════════════════════");
        System.out.println("Thread ID : " + Thread.currentThread().getId());
        System.out.println("Browser   : " + browser);
        System.out.println("Env       : " + env);
        System.out.println("══════════════════════════════════════");

        WebDriver driver = createDriver(browser);
        driver.manage().window().maximize();

        // Lưu driver vào ThreadLocal cho thread hiện tại
        driverThreadLocal.set(driver);

        // Cấu hình URL base theo môi trường
        String baseUrl = resolveBaseUrl(env);
        System.out.println("Base URL  : " + baseUrl);
    }

    /**
     * Tạo WebDriver theo loại browser được chỉ định.
     *
     * @param browser "chrome" hoặc "firefox"
     * @return WebDriver instance đã được cấu hình
     */
    private WebDriver createDriver(String browser) {
        switch (browser.toLowerCase().trim()) {
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                return new FirefoxDriver(firefoxOptions);

            case "chrome":
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                // Thêm options để ổn định khi chạy song song
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                chromeOptions.addArguments("--disable-gpu");
                chromeOptions.addArguments("--remote-allow-origins=*");
                return new ChromeDriver(chromeOptions);
        }
    }

    /**
     * Trả về base URL tương ứng với môi trường.
     *
     * @param env tên môi trường
     * @return URL của môi trường đó
     */
    private String resolveBaseUrl(String env) {
        switch (env.toLowerCase().trim()) {
            case "staging":
                return "https://staging.saucedemo.com";
            case "prod":
                return "https://www.saucedemo.com";
            case "dev":
            default:
                return "https://www.saucedemo.com";
        }
    }

    // =========================================================
    // @AfterMethod - Chụp ảnh khi fail, đóng browser
    // =========================================================

    /**
     * Chụp ảnh màn hình khi test FAIL và đóng browser sau mỗi test method.
     *
     * <p>
     * <b>Quy tắc đặt tên file screenshot:</b>
     * </p>
     * 
     * <pre>
     * {testClassName}_{testMethodName}_{yyyyMMdd_HHmmss_SSS}.png
     * </pre>
     *
     * <p>
     * File được lưu vào: {@code target/screenshots/}
     * </p>
     *
     * <p>
     * Phương thức nhận {@link ITestResult} để biết test có bị FAIL không
     * và lấy tên test method để đặt tên file.
     * </p>
     *
     * @param result kết quả của test method vừa chạy (passed, failed, skipped)
     */
    @AfterMethod
    public void tearDown(ITestResult result) {
        WebDriver driver = driverThreadLocal.get();

        try {
            // Chỉ chụp ảnh khi test FAIL
            if (result.getStatus() == ITestResult.FAILURE) {
                takeScreenshot(driver, result);
            }
        } finally {
            // Luôn đóng browser dù test pass hay fail
            if (driver != null) {
                driver.quit();
                // Xóa driver ra khỏi ThreadLocal để tránh memory leak
                driverThreadLocal.remove();
            }
        }
    }

    /**
     * Chụp ảnh màn hình và lưu vào thư mục target/screenshots/.
     *
     * <p>
     * <b>Định dạng tên file:</b> {@code {testName}_{timestamp}.png}
     * </p>
     * <p>
     * Trong đó {@code testName} = {@code ClassName_methodName}
     * </p>
     *
     * @param driver WebDriver của thread hiện tại
     * @param result ITestResult chứa thông tin tên test
     */
    private void takeScreenshot(WebDriver driver, ITestResult result) {
        if (driver == null) {
            System.err.println("[Screenshot] Driver is null, cannot take screenshot.");
            return;
        }

        try {
            // Tạo thư mục target/screenshots/ nếu chưa tồn tại
            Path screenshotDir = Paths.get(SCREENSHOT_DIR);
            Files.createDirectories(screenshotDir);

            // Tên test: ClassName_methodName
            String testName = result.getTestClass().getRealClass().getSimpleName()
                    + "_" + result.getMethod().getMethodName();

            // Timestamp để tránh trùng tên khi chạy song song
            String timestamp = new SimpleDateFormat(SCREENSHOT_TIMESTAMP_FORMAT)
                    .format(new Date());

            // Tên file: {testName}_{timestamp}.png
            String fileName = testName + "_" + timestamp + ".png";
            Path targetPath = screenshotDir.resolve(fileName);

            // Chụp ảnh và lưu file
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(screenshot.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("[Screenshot] SAVED → " + targetPath.toAbsolutePath());

        } catch (IOException e) {
            System.err.println("[Screenshot] FAILED to save: " + e.getMessage());
        } catch (WebDriverException e) {
            System.err.println("[Screenshot] Browser closed before screenshot: " + e.getMessage());
        }
    }
}
