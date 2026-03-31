package com.lab9.bai2;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * BaseTest - Lớp nền tảng cho tất cả Test Class (Bài 2).
 *
 * <ul>
 * <li>ThreadLocal WebDriver — hỗ trợ parallel không xung đột</li>
 * <li>@BeforeMethod nhận browser + env từ testng.xml (@Optional có
 * default)</li>
 * <li>@AfterMethod chụp ảnh khi FAIL →
 * target/screenshots/{testName}_{timestamp}.png</li>
 * </ul>
 */
public class BaseTest {

    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    public WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    @BeforeMethod
    @Parameters({ "browser", "env" })
    public void setUp(@Optional("chrome") String browser, @Optional("dev") String env) {
        System.out.printf("══ Thread=%d | Browser=%s | Env=%s ══%n",
                Thread.currentThread().getId(), browser, env);

        WebDriver driver = createDriver(browser);
        driver.manage().window().maximize();
        driverThreadLocal.set(driver);
    }

    private WebDriver createDriver(String browser) {
        if ("firefox".equalsIgnoreCase(browser)) {
            WebDriverManager.firefoxdriver().setup();
            return new FirefoxDriver();
        }
        WebDriverManager.chromedriver().setup();
        ChromeOptions opts = new ChromeOptions();
        opts.addArguments("--no-sandbox", "--disable-dev-shm-usage",
                "--disable-gpu", "--remote-allow-origins=*");
        return new ChromeDriver(opts);
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
            }
            driverThreadLocal.remove();
        }
    }

    private void takeScreenshot(WebDriver driver, ITestResult result) {
        if (driver == null)
            return;
        try {
            Path dir = Paths.get("target/screenshots");
            Files.createDirectories(dir);
            String name = result.getTestClass().getRealClass().getSimpleName()
                    + "_" + result.getMethod().getMethodName()
                    + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date())
                    + ".png";
            java.io.File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(src.toPath(), dir.resolve(name), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("[Screenshot] → target/screenshots/" + name);
        } catch (IOException | WebDriverException e) {
            System.err.println("[Screenshot] Lỗi: " + e.getMessage());
        }
    }
}
