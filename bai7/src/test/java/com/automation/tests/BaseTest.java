package com.automation.tests;

import com.automation.framework.DriverFactory;
import io.qameta.allure.Attachment;
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
 * <h2>BaseTest - Tích hợp Allure Report Attachment</h2>
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
        WebDriver driver = DriverFactory.createDriver(browser);
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
                // Chụp ảnh truyền thống
                takeScreenshot(driver, result);
                // Đính kèm vào Allure Report
                attachScreenshot(driver);
            }
        } finally {
            if (driver != null) {
                driver.quit();
                driverThreadLocal.remove();
            }
        }
    }

    /**
     * Đính kèm ảnh chụp màn hình vào Allure Report
     */
    @Attachment(value = "Ảnh chụp khi thất bại", type = "image/png")
    public byte[] attachScreenshot(WebDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    private void takeScreenshot(WebDriver driver, ITestResult result) {
        if (driver == null) return;
        try {
            Path screenshotDir = Paths.get(SCREENSHOT_DIR);
            Files.createDirectories(screenshotDir);
            String testName = result.getTestClass().getRealClass().getSimpleName() + "_" + result.getMethod().getMethodName();
            String timestamp = new SimpleDateFormat(SCREENSHOT_TIMESTAMP_FORMAT).format(new Date());
            String fileName = testName + "_" + timestamp + ".png";
            Path targetPath = screenshotDir.resolve(fileName);
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(screenshot.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException | WebDriverException ignored) {
        }
    }
}
