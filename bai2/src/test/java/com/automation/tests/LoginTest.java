package com.automation.tests;

import com.automation.framework.TestDataReader;
import com.automation.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * <h2>LoginTest - Test class kiểm thử đăng nhập SauceDemo</h2>
 *
 * <p>Kế thừa BaseTest để tự động có WebDriver (headless trên CI),
 * setup/teardown, và chụp ảnh khi fail.</p>
 *
 * @author Lab11 - CI/CD Framework
 * @version 1.0
 */
public class LoginTest extends BaseTest {

    private static final String LOGIN_DATA = "testdata/login_data.json";

    /**
     * TC01 - Đăng nhập thành công với tài khoản hợp lệ.
     * Kỳ vọng: Trang Products hiển thị sau khi login.
     */
    @Test(description = "TC01 - Đăng nhập thành công")
    public void testLoginSuccess() {
        Map<String, String> data = TestDataReader.getTestCase(LOGIN_DATA, "TC01_LoginSuccess");
        String username = data.get("username");
        String password = data.get("password");

        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.openLoginPage()
                .login(username, password);

        Assert.assertTrue(
                loginPage.isLoginSuccessful(),
                "Sau khi đăng nhập, trang Products phải hiển thị");
    }

    /**
     * TC02 - Đăng nhập thất bại với mật khẩu sai.
     * Kỳ vọng: Hiển thị thông báo lỗi "Username and password do not match".
     */
    @Test(description = "TC02 - Đăng nhập thất bại - mật khẩu sai")
    public void testLoginWrongPassword() {
        Map<String, String> data = TestDataReader.getTestCase(LOGIN_DATA, "TC02_LoginWrongPassword");
        String username = data.get("username");
        String password = data.get("password");
        String expectedError = data.get("expectedResult");

        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.openLoginPage()
                .login(username, password);

        Assert.assertTrue(
                loginPage.isErrorDisplayed(),
                "Phải hiển thị thông báo lỗi khi mật khẩu sai");

        String actualError = loginPage.getErrorMessage();
        Assert.assertTrue(
                actualError.contains(expectedError),
                "Thông báo lỗi không đúng.\nExpected contains: " + expectedError
                        + "\nActual: " + actualError);
    }

    /**
     * TC03 - Đăng nhập với tài khoản bị khóa.
     * Kỳ vọng: Hiển thị thông báo "Sorry, this user has been locked out."
     */
    @Test(description = "TC03 - Đăng nhập với tài khoản bị locked")
    public void testLoginLockedUser() {
        Map<String, String> data = TestDataReader.getTestCase(LOGIN_DATA, "TC03_LoginLockedUser");
        String username = data.get("username");
        String password = data.get("password");
        String expectedError = data.get("expectedResult");

        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.openLoginPage()
                .login(username, password);

        Assert.assertTrue(
                loginPage.isErrorDisplayed(),
                "Phải hiển thị thông báo lỗi với tài khoản bị khóa");

        String actualError = loginPage.getErrorMessage();
        Assert.assertTrue(
                actualError.contains(expectedError),
                "Thông báo lỗi không đúng.\nExpected contains: " + expectedError
                        + "\nActual: " + actualError);
    }
}
