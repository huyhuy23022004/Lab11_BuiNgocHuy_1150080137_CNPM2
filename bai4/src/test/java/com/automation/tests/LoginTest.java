package com.automation.tests;

import com.automation.framework.ConfigReader;
import com.automation.framework.TestDataReader;
import com.automation.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.Map;

public class LoginTest extends BaseTest {

    private static final String LOGIN_DATA = "testdata/login_data.json";

    private String resolveCredential(String value) {
        if ("<ENV_USERNAME>".equals(value)) return ConfigReader.getAppUsername();
        if ("<ENV_PASSWORD>".equals(value)) return ConfigReader.getAppPassword();
        return value;
    }

    @Test(description = "TC01 - Đăng nhập thành công")
    public void testLoginSuccess() {
        Map<String, String> data = TestDataReader.getTestCase(LOGIN_DATA, "TC01_LoginSuccess");
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.openLoginPage().login(resolveCredential(data.get("username")), resolveCredential(data.get("password")));
        Assert.assertTrue(loginPage.isLoginSuccessful());
    }

    @Test(description = "TC02 - Sai mật khẩu")
    public void testLoginWrongPassword() {
        Map<String, String> data = TestDataReader.getTestCase(LOGIN_DATA, "TC02_LoginWrongPassword");
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.openLoginPage().login(resolveCredential(data.get("username")), "wrong_pass");
        Assert.assertTrue(loginPage.isErrorDisplayed());
    }

    @Test(description = "TC03 - Tài khoản bị khóa")
    public void testLoginLockedUser() {
        Map<String, String> data = TestDataReader.getTestCase(LOGIN_DATA, "TC03_LoginLockedUser");
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.openLoginPage().login(data.get("username"), resolveCredential(data.get("password")));
        Assert.assertTrue(loginPage.isErrorDisplayed());
    }

    @Test(description = "TC04 - Username để trống")
    public void testLoginEmptyUsername() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.openLoginPage().login("", "secret_sauce");
        Assert.assertTrue(loginPage.isErrorDisplayed());
        Assert.assertTrue(loginPage.getErrorMessage().contains("Username is required"));
    }

    @Test(description = "TC05 - Password để trống")
    public void testLoginEmptyPassword() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.openLoginPage().login("standard_user", "");
        Assert.assertTrue(loginPage.isErrorDisplayed());
        Assert.assertTrue(loginPage.getErrorMessage().contains("Password is required"));
    }
}
