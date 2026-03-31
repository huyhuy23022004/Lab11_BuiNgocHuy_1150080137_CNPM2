package com.lab9.bai5;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

public class BaseTest {

    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    public WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    /**
     * Xử lý đa môi trường bằng Setup của TestNG Parameter kết hợp gán
     * System.setProperty.
     */
    @BeforeMethod(alwaysRun = true)
    @Parameters({ "env" })
    public void setUp(@Optional("dev") String envParam) {
        // Lấy property từ lệnh 'mvn test -Denv=' nếu có
        String sysEnv = System.getProperty("env");

        // Nếu giá trị từ maven là tồn tại và khác literal null của xml ('${env}') - Ghi
        // đè giá trị ENV vào test suite
        if (sysEnv != null && !sysEnv.isEmpty() && !sysEnv.equals("${env}")) {
            envParam = sysEnv;
        }

        // --- YÊU CẦU ĐỀ: Gọi System.setProperty("env", env) trước khi khởi tạo
        // ConfigReader ---
        System.setProperty("env", envParam);
        ConfigReader config = ConfigReader.getInstance();

        // Thỏa mãn Demo logic (in ra Console) log URL + ExplicitWait
        System.out.println("\n#############################################################");
        System.out.println("Đang dùng môi trường: " + envParam);
        System.out.println("URL hiện tại      : " + config.getBaseUrl());
        System.out.println("explicit wait = " + config.getExplicitWait());
        System.out.println("#############################################################\n");

        WebDriverManager.chromedriver().setup();
        ChromeOptions opts = new ChromeOptions();
        opts.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--disable-gpu", "--remote-allow-origins=*");
        WebDriver driver = new ChromeDriver(opts);
        driver.manage().window().maximize();
        driverThreadLocal.set(driver);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            driver.quit();
            driverThreadLocal.remove();
        }
    }
}
