package tests;

import factory.DriverFactory;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * BaseTest - Lớp nền tảng cho tất cả Test Class của Lab 11 sửa đổi.
 */
public class BaseTest {

    public WebDriver getDriver() {
        return DriverFactory.getDriver();
    }

    @BeforeMethod
    @Parameters({ "browser", "env" })
    public void setUp(@Optional("chrome") String browser, @Optional("dev") String env) {
        System.out.printf("══ Thread=%d | Browser=%s | Env=%s ══%n",
                Thread.currentThread().getId(), browser, env);

        // Khai báo kết nối từ DriverFactory thay vì tạo mới rườm rà
        DriverFactory.createDriver(browser);
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        WebDriver driver = DriverFactory.getDriver();
        try {
            if (result.getStatus() == ITestResult.FAILURE) {
                takeScreenshot(driver, result);
            }
        } finally {
            DriverFactory.quitDriver();
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
