# BÀI 5: ALLURE REPORT NÂNG CAO VỚI ANNOTATION (1.0 điểm)

### 5.1 Thêm Allure vào pom.xml
```xml
<properties>
    <allure.version>2.26.0</allure.version>
</properties>

<dependencies>
    <dependency>
        <groupId>io.qameta.allure</groupId>
        <artifactId>allure-testng</artifactId>
        <version>${allure.version}</version>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>io.qameta.allure</groupId>
            <artifactId>allure-maven</artifactId>
            <version>2.12.0</version>
        </plugin>
    </plugins>
</build>
```

### 5.2 Thêm Annotation vào test class
```java
@Test
@Feature("Đăng nhập hệ thống")
@Story("UC-001: Đăng nhập bằng tài khoản hợp lệ")
@Description("Kiểm thử đăng nhập với username/password hợp lệ")
@Severity(SeverityLevel.CRITICAL)
public void testLoginSuccess() {
    Allure.step("Mở trang đăng nhập", () -> driver.get(BASE_URL));
    Allure.step("Nhập thông tin đăng nhập", () -> {
        loginPage.enterUsername("standard_user");
        loginPage.enterPassword("secret_sauce");
    });
    Allure.step("Click nút Đăng nhập", () -> loginPage.clickLoginButton());
    Allure.step("Kiểm tra chuyển trang thành công", () ->
        Assert.assertTrue(driver.getCurrentUrl().contains("inventory")));
}
```

### 5.3 Đính kèm ảnh chụp khi test FAIL
```java
// Thêm vào BaseTest.java
@AfterMethod
public void tearDown(ITestResult result) {
    if (result.getStatus() == ITestResult.FAILURE) {
        attachScreenshot(driver);
    }
    driver.quit();
}

@Attachment(value = "Ảnh chụp khi thất bại", type = "image/png")
public byte[] attachScreenshot(WebDriver driver) {
    return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
}
```

### 5.4 Chạy và xem Allure Report
```bash
mvn clean test        # Chạy test, tạo thư mục target/allure-results/
mvn allure:serve      # Tự động mở báo cáo trong trình duyệt

# Hoặc tạo file HTML tĩnh:
mvn allure:report     # Tạo tại target/site/allure-maven-plugin/index.html
```

### Tiêu chí chấm điểm
- [ ] `pom.xml` có `allure-testng` dependency và `allure-maven` plugin
- [ ] `LoginTest` và `CartTest` có `@Feature`, `@Story`, `@Severity`, `@Description`
- [ ] Mỗi test có `Allure.step()` ghi lại từng bước thực hiện
- [ ] `BaseTest` có `@AfterMethod` đính kèm ảnh PNG khi test fail
- [ ] Chụp màn hình Allure Report: biểu đồ pass/fail, step-by-step, ảnh khi fail
