package com.lab9.bai3;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BaseTest {

    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    public WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions opts = new ChromeOptions();
        opts.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--disable-gpu", "--remote-allow-origins=*");
        WebDriver driver = new ChromeDriver(opts);
        driver.manage().window().maximize();
        driverThreadLocal.set(driver);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                if (result.getStatus() == ITestResult.FAILURE) {
                    takeScreenshot(driver, result);
                }
            } finally {
                driver.quit();
                driverThreadLocal.remove();
            }
        }
    }

    private void takeScreenshot(WebDriver driver, ITestResult result) {
        try {
            Path dir = Paths.get("target/screenshots");
            Files.createDirectories(dir);

            // Format tên ảnh theo yêu cầu: rõ ràng từ phần tên test case
            String testName = result.getTestName();
            if (testName == null || testName.isEmpty()) {
                // Tên description bị trống thì dùng test class làm fallback
                testName = result.getMethod().getMethodName();
            }

            // Xử lý loại bỏ các ký tự không hợp lệ với đường dẫn tệp File OS Windows
            testName = testName.replaceAll("[\\\\/:*?\"<>|]", "_");
            String filename = testName + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date()) + ".png";

            java.io.File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(src.toPath(), dir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("[Screenshot] Bị lỗi! Chụp được hình ở: target/screenshots/" + filename);
        } catch (IOException | WebDriverException e) {
            System.err.println("[Screenshot] Lỗi: " + e.getMessage());
        }
    }
}
