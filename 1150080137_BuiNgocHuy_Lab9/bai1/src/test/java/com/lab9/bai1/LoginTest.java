package com.lab9.bai1;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * <h2>LoginTest - Test class kiểm thử đăng nhập SauceDemo</h2>
 *
 * <p>
 * Kế thừa BaseTest để tự động có WebDriver, setup/teardown, và chụp ảnh khi
 * fail.
 * </p>
 *
 * <h3>Tuân thủ 4 quy tắc framework:</h3>
 * <ol>
 * <li>Mọi @Test dùng {@code getDriver()} từ BaseTest - không tạo WebDriver
 * mới</li>
 * <li>Mọi {@code driver.findElement()} nằm trong Page Object
 * (LoginPage/BasePage)</li>
 * <li>Dữ liệu test (username, password, URL) đọc từ file JSON và
 * config.properties</li>
 * <li>Không dùng {@code Thread.sleep()} ở bất kỳ đâu</li>
 * </ol>
 *
 * <h3>Kiểm tra song song (parallel):</h3>
 * <p>
 * Khi chạy với testng.xml có {@code parallel="methods" thread-count="3"},
 * 3 test method sẽ mở 3 cửa sổ Chrome cùng lúc mà KHÔNG xung đột
 * nhờ cơ chế ThreadLocal trong BaseTest.
 * </p>
 *
 * @author Lab9 - Page Object Model Framework
 * @version 1.0
 */
public class LoginTest extends BaseTest {

        /** Đường dẫn file test data JSON (tương đối từ classpath) */
        private static final String LOGIN_DATA = "testdata/login_data.json";

        // =========================================================
        // TEST METHODS
        // =========================================================

        /**
         * TC01 - Đăng nhập thành công với tài khoản hợp lệ.
         *
         * <p>
         * <b>Dữ liệu:</b> đọc từ {@code testdata/login_data.json} — testCase
         * "TC01_LoginSuccess"
         * </p>
         * <p>
         * <b>Kỳ vọng:</b> Trang Products hiển thị sau khi login.
         * </p>
         */
        @Test(description = "TC01 - Đăng nhập thành công")
        public void testLoginSuccess() {
                // Đọc dữ liệu từ JSON — KHÔNG hardcode username/password
                Map<String, String> data = TestDataReader.getTestCase(LOGIN_DATA, "TC01_LoginSuccess");
                String username = data.get("username");
                String password = data.get("password");

                // Dùng getDriver() từ BaseTest — KHÔNG tạo WebDriver mới
                LoginPage loginPage = new LoginPage(getDriver());

                loginPage.openLoginPage() // URL lấy từ config.properties
                                .login(username, password);

                Assert.assertTrue(
                                loginPage.isLoginSuccessful(),
                                "Sau khi đăng nhập, trang Products phải hiển thị");
        }

        /**
         * TC02 - Đăng nhập thất bại với mật khẩu sai.
         *
         * <p>
         * <b>Dữ liệu:</b> đọc từ {@code testdata/login_data.json} — testCase
         * "TC02_LoginWrongPassword"
         * </p>
         * <p>
         * <b>Kỳ vọng:</b> Hiển thị thông báo lỗi "Username and password do not match".
         * </p>
         */
        @Test(description = "TC02 - Đăng nhập thất bại - mật khẩu sai")
        public void testLoginWrongPassword() {
                // Đọc dữ liệu từ JSON — KHÔNG hardcode
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
         *
         * <p>
         * <b>Dữ liệu:</b> đọc từ {@code testdata/login_data.json} — testCase
         * "TC03_LoginLockedUser"
         * </p>
         * <p>
         * <b>Kỳ vọng:</b> Hiển thị thông báo "Sorry, this user has been locked out."
         * </p>
         */
        @Test(description = "TC03 - Đăng nhập với tài khoản bị locked")
        public void testLoginLockedUser() {
                // Đọc dữ liệu từ JSON — KHÔNG hardcode
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
