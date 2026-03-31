package com.automation.tests;

import com.automation.framework.ConfigReader;
import com.automation.framework.TestDataReader;
import com.automation.pages.LoginPage;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.Map;

@Feature("Quản lý Đăng nhập")
public class LoginTest extends BaseTest {

    private static final String LOGIN_DATA = "testdata/login_data.json";

    private String resolveCredential(String value) {
        if ("<ENV_USERNAME>".equals(value)) return ConfigReader.getAppUsername();
        if ("<ENV_PASSWORD>".equals(value)) return ConfigReader.getAppPassword();
        return value;
    }

    @Test(description = "TC01 - Đăng nhập thành công với tài khoản hợp lệ")
    @Story("Đăng nhập thành công")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm thử đăng nhập vào hệ thống với thông tin chính danh.")
    public void testLoginSuccess() {
        Map<String, String> data = TestDataReader.getTestCase(LOGIN_DATA, "TC01_LoginSuccess");
        String user = resolveCredential(data.get("username"));
        String pass = resolveCredential(data.get("password"));

        LoginPage loginPage = new LoginPage(getDriver());
        
        Allure.step("Mở trang đăng nhập", loginPage::openLoginPage);
        Allure.step("Nhập thông tin và click Login", () -> loginPage.login(user, pass));
        
        Allure.step("Kiểm tra đăng nhập thành công", () -> {
            Assert.assertTrue(loginPage.isLoginSuccessful(), "Chưa thấy trang Products!");
        });
    }

    @Test(description = "TC02 - Sai mật khẩu hiển thị thông báo lỗi")
    @Story("Đăng nhập thất bại")
    @Severity(SeverityLevel.NORMAL)
    public void testLoginWrongPassword() {
        Map<String, String> data = TestDataReader.getTestCase(LOGIN_DATA, "TC02_LoginWrongPassword");
        LoginPage loginPage = new LoginPage(getDriver());
        
        Allure.step("Mở trang đăng nhập", loginPage::openLoginPage);
        Allure.step("Đăng nhập với pass sai", () -> loginPage.login(resolveCredential(data.get("username")), "wrong_pass"));
        
        Allure.step("Kiểm tra thông báo lỗi hiển thị", () -> {
            Assert.assertTrue(loginPage.isErrorDisplayed());
            Assert.assertTrue(loginPage.getErrorMessage().contains(data.get("expectedResult")));
        });
    }

    @Test(description = "TC04 - Username rỗng")
    @Story("Cảnh báo để trống trường thông tin đăng nhập")
    @Severity(SeverityLevel.MINOR)
    public void testLoginEmptyUsername() {
        LoginPage loginPage = new LoginPage(getDriver());
        Allure.step("Mở trang đăng nhập", loginPage::openLoginPage);
        Allure.step("Để trống username", () -> loginPage.login("", "secret_sauce"));
        Allure.step("Kiểm tra cảnh báo lỗi", () -> {
            Assert.assertTrue(loginPage.getErrorMessage().contains("Username is required"));
        });
    }
}
