package com.lab9.bai1;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * <h2>LoginPage - Page Object cho trang đăng nhập SauceDemo</h2>
 *
 * <p>
 * Minh họa cách sử dụng BasePage trong một Page Object thực tế.
 * Tất cả locator và thao tác liên quan đến Login đều được đặt ở đây,
 * giúp test class không cần quan tâm đến chi tiết UI.
 * </p>
 *
 * <p>
 * <b>Quy tắc:</b> URL không được hardcode — đọc từ {@link ConfigReader}
 * </p>
 *
 * @author Lab9 - Page Object Model Framework
 * @version 1.0
 */
public class LoginPage extends BasePage {

    // =========================================================
    // LOCATORS - Tập trung tại một chỗ, dễ maintain
    // =========================================================
    private static final By USERNAME_INPUT = By.id("user-name");
    private static final By PASSWORD_INPUT = By.id("password");
    private static final By LOGIN_BUTTON = By.id("login-button");
    private static final By ERROR_MESSAGE = By.cssSelector("[data-test='error']");
    private static final By PRODUCT_TITLE = By.cssSelector(".title");

    /** URL trang đăng nhập - đọc từ config.properties, KHÔNG hardcode */
    private final String baseUrl;

    /**
     * Khởi tạo LoginPage. URL tự động đọc từ {@link ConfigReader} với env="dev".
     *
     * @param driver WebDriver ThreadLocal-safe từ BaseTest
     */
    public LoginPage(WebDriver driver) {
        super(driver);
        this.baseUrl = ConfigReader.getBaseUrl("dev");
    }

    /**
     * Khởi tạo LoginPage với môi trường cụ thể.
     *
     * @param driver WebDriver ThreadLocal-safe từ BaseTest
     * @param env    môi trường: "dev", "staging", "prod"
     */
    public LoginPage(WebDriver driver, String env) {
        super(driver);
        this.baseUrl = ConfigReader.getBaseUrl(env);
    }

    // =========================================================
    // PAGE METHODS
    // =========================================================

    /**
     * Mở trang đăng nhập và chờ trang tải hoàn toàn.
     * URL lấy từ {@link ConfigReader} — không hardcode.
     *
     * @return LoginPage (fluent interface)
     */
    public LoginPage openLoginPage() {
        driver.get(baseUrl); // URL từ config.properties
        waitForPageLoad();
        return this;
    }

    /**
     * Thực hiện đăng nhập với username và password.
     *
     * @param username tên đăng nhập
     * @param password mật khẩu
     * @return LoginPage (fluent interface)
     */
    public LoginPage login(String username, String password) {
        waitAndType(USERNAME_INPUT, username);
        waitAndType(PASSWORD_INPUT, password);
        waitAndClick(LOGIN_BUTTON);
        return this;
    }

    /**
     * Lấy text thông báo lỗi khi đăng nhập thất bại.
     *
     * @return nội dung thông báo lỗi
     */
    public String getErrorMessage() {
        return getText(ERROR_MESSAGE);
    }

    /**
     * Kiểm tra đã đăng nhập thành công hay chưa (dựa vào tiêu đề "Products").
     *
     * @return true nếu trang Products hiển thị
     */
    public boolean isLoginSuccessful() {
        return isElementVisible(PRODUCT_TITLE);
    }

    /**
     * Kiểm tra thông báo lỗi có hiển thị không.
     *
     * @return true nếu có error message
     */
    public boolean isErrorDisplayed() {
        return isElementVisible(ERROR_MESSAGE);
    }

    /**
     * Lấy giá trị placeholder của ô username.
     *
     * @return placeholder text
     */
    public String getUsernamePlaceholder() {
        return getAttribute(USERNAME_INPUT, "placeholder");
    }
}
