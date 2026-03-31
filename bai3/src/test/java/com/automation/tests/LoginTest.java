package com.automation.tests;

import com.automation.framework.ConfigReader;
import com.automation.framework.TestDataReader;
import com.automation.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * <h2>LoginTest - Test class kiểm thử đăng nhập SauceDemo</h2>
 *
 * <p>Thay vì hardcode password, đọc từ biến môi trường (qua ConfigReader).</p>
 *
 * @author Lab11 - CI/CD Framework
 * @version 1.0
 */
public class LoginTest extends BaseTest {

    private static final String LOGIN_DATA = "testdata/login_data.json";

    /**
     * Resolves username/password credentials.
     * Use <ENV_USERNAME> and <ENV_PASSWORD> strings in JSON to fetch from Secrets.
     */
    private String resolveCredential(String value) {
        if ("<ENV_USERNAME>".equals(value)) {
            return ConfigReader.getAppUsername();
        } else if ("<ENV_PASSWORD>".equals(value)) {
            return ConfigReader.getAppPassword();
        }
        return value;
    }

    @Test(description = "TC01 - Đăng nhập thành công")
    public void testLoginSuccess() {
        Map<String, String> data = TestDataReader.getTestCase(LOGIN_DATA, "TC01_LoginSuccess");
        String username = resolveCredential(data.get("username"));
        String password = resolveCredential(data.get("password"));

        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.openLoginPage().login(username, password);

        Assert.assertTrue(
                loginPage.isLoginSuccessful(),
                "Sau khi đăng nhập, trang Products phải hiển thị");
    }

    @Test(description = "TC02 - Đăng nhập thất bại - mật khẩu sai")
    public void testLoginWrongPassword() {
        Map<String, String> data = TestDataReader.getTestCase(LOGIN_DATA, "TC02_LoginWrongPassword");
        // For wrong password, we typically actually use a wrong placeholder string
        String username = resolveCredential(data.get("username"));
        String password = resolveCredential(data.get("password"));
        String expectedError = data.get("expectedResult");

        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.openLoginPage().login(username, password);

        Assert.assertTrue(loginPage.isErrorDisplayed(), "Phải hiển thị thông báo lỗi khi mật khẩu sai");
        Assert.assertTrue(loginPage.getErrorMessage().contains(expectedError));
    }

    @Test(description = "TC03 - Đăng nhập với tài khoản bị locked")
    public void testLoginLockedUser() {
        Map<String, String> data = TestDataReader.getTestCase(LOGIN_DATA, "TC03_LoginLockedUser");
        String username = resolveCredential(data.get("username"));
        String password = resolveCredential(data.get("password"));
        String expectedError = data.get("expectedResult");

        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.openLoginPage().login(username, password);

        Assert.assertTrue(loginPage.isErrorDisplayed(), "Phải hiển thị thông báo lỗi với tài khoản bị khóa");
        Assert.assertTrue(loginPage.getErrorMessage().contains(expectedError));
    }
}
