package com.automation.tests;

import com.automation.framework.DriverFactory;
import org.openqa.selenium.*;
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
 * <h2>BaseTest - Lớp nền tảng cho tất cả Test Class (Lab 11)</h2>
 *
 * <p>Sử dụng DriverFactory mới với hỗ trợ Headless mode cho CI/CD.
 * ThreadLocal đảm bảo chạy parallel an toàn.</p>
 *
 * @author Lab11 - CI/CD Framework
 * @version 1.0
 */
public class BaseTest {

    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private static final String SCREENSHOT_TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss_SSS";
    private static final String SCREENSHOT_DIR = "target/screenshots";

    public WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    @BeforeMethod
    @Parameters({"browser", "env"})
    public void setUp(
            @Optional("chrome") String browser,
            @Optional("dev") String env) {
        System.out.println("══════════════════════════════════════");
        System.out.println("Thread ID : " + Thread.currentThread().getId());
        System.out.println("Browser   : " + browser);
        System.out.println("Env       : " + env);
        System.out.println("CI Mode   : " + (System.getenv("CI") != null));
        System.out.println("══════════════════════════════════════");

        // Sử dụng DriverFactory MỚI - tự động bật headless trên CI
        WebDriver driver = DriverFactory.createDriver(browser);

        // Chỉ maximize khi KHÔNG ở CI (headless đã set window-size)
        if (System.getenv("CI") == null) {
            driver.manage().window().maximize();
        }

        driverThreadLocal.set(driver);
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        WebDriver driver = driverThreadLocal.get();

        try {
            if (result.getStatus() == ITestResult.FAILURE) {
                takeScreenshot(driver, result);
            }
        } finally {
            if (driver != null) {
                driver.quit();
                driverThreadLocal.remove();
            }
        }
    }

    private void takeScreenshot(WebDriver driver, ITestResult result) {
        if (driver == null) {
            System.err.println("[Screenshot] Driver is null, cannot take screenshot.");
            return;
        }

        try {
            Path screenshotDir = Paths.get(SCREENSHOT_DIR);
            Files.createDirectories(screenshotDir);

            String testName = result.getTestClass().getRealClass().getSimpleName()
                    + "_" + result.getMethod().getMethodName();

            String timestamp = new SimpleDateFormat(SCREENSHOT_TIMESTAMP_FORMAT)
                    .format(new Date());

            String fileName = testName + "_" + timestamp + ".png";
            Path targetPath = screenshotDir.resolve(fileName);

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
