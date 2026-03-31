package tests;

import pages.*;
import utils.*;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * <h2>LoginTest - Kiểm thử trang đăng nhập SauceDemo (Bài 2)</h2>
 *
 * <p>
 * Kế thừa BaseTest. Dùng Fluent Interface qua LoginPage.
 * </p>
 *
 * <p>
 * <b>Quy tắc POM được tuân thủ:</b>
 * </p>
 * <ul>
 * <li>KHÔNG có By.id() hay driver.findElement() trong class này</li>
 * <li>Dữ liệu đọc từ JSON — không hardcode</li>
 * <li>Không dùng Thread.sleep()</li>
 * <li>Dùng getDriver() từ BaseTest</li>
 * </ul>
 */
public class LoginTest extends BaseTest {

    private static final String LOGIN_DATA = "testdata/login_data.json";

    /**
     * TC01 - Đăng nhập thành công.
     * Fluent: loginPage.open().login(u,p) → InventoryPage
     */
    @Test(description = "TC01 - Đăng nhập thành công → InventoryPage hiển thị")
    public void testLoginSuccess() {
        Map<String, String> data = TestDataReader.getTestCase(LOGIN_DATA, "TC01_LoginSuccess");

        InventoryPage inventoryPage = new LoginPage(getDriver())
                .open()
                .login(data.get("username"), data.get("password")); // login() → InventoryPage

        Assert.assertTrue(
                inventoryPage.isLoaded(),
                "Sau khi login thành công, trang Inventory phải hiển thị");
    }

    /**
     * TC02 - Đăng nhập sai mật khẩu.
     * Fluent: loginPage.open().loginExpectingFailure(u,p) → LoginPage còn lỗi
     */
    @Test(description = "TC02 - Đăng nhập sai mật khẩu → hiển thị error")
    public void testLoginWrongPassword() {
        Map<String, String> data = TestDataReader.getTestCase(LOGIN_DATA, "TC02_LoginWrongPassword");

        LoginPage loginPage = new LoginPage(getDriver())
                .open()
                .loginExpectingFailure(data.get("username"), data.get("password")); // → LoginPage

        Assert.assertTrue(loginPage.isErrorDisplayed(), "Error message phải hiển thị");
        Assert.assertTrue(
                loginPage.getErrorMessage().contains(data.get("expectedResult")),
                "Nội dung lỗi không đúng. Actual: " + loginPage.getErrorMessage());
    }

    /**
     * TC03 - Đăng nhập tài khoản bị khóa.
     */
    @Test(description = "TC03 - Đăng nhập tài khoản bị locked → hiển thị error")
    public void testLoginLockedUser() {
        Map<String, String> data = TestDataReader.getTestCase(LOGIN_DATA, "TC03_LoginLockedUser");

        LoginPage loginPage = new LoginPage(getDriver())
                .open()
                .loginExpectingFailure(data.get("username"), data.get("password"));

        Assert.assertTrue(loginPage.isErrorDisplayed(), "Error message phải hiển thị với locked user");
        Assert.assertTrue(
                loginPage.getErrorMessage().contains(data.get("expectedResult")),
                "Nội dung lỗi không đúng. Actual: " + loginPage.getErrorMessage());
    }
}
