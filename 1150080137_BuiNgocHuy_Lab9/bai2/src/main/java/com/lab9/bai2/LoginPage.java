package com.lab9.bai2;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * <h2>LoginPage - Page Object cho trang đăng nhập SauceDemo</h2>
 *
 * <h3>Fluent Interface:</h3>
 * 
 * <pre>{@code
 * InventoryPage inv = loginPage.login("standard_user", "secret_sauce");
 * LoginPage lp = loginPage.loginExpectingFailure("bad", "bad");
 * }</pre>
 *
 * <p>
 * <b>Quy tắc POM:</b> Không có driver.findElement() hay By.id() trong test
 * class.
 * </p>
 */
public class LoginPage extends BasePage {

    // ── Locators ──────────────────────────────────────────────
    private static final By USERNAME_INPUT = By.id("user-name");
    private static final By PASSWORD_INPUT = By.id("password");
    private static final By LOGIN_BUTTON = By.id("login-button");
    private static final By ERROR_MESSAGE = By.cssSelector("[data-test='error']");

    /** URL từ ConfigReader — không hardcode */
    private final String baseUrl;

    public LoginPage(WebDriver driver) {
        super(driver);
        this.baseUrl = ConfigReader.getBaseUrl("dev");
    }

    public LoginPage(WebDriver driver, String env) {
        super(driver);
        this.baseUrl = ConfigReader.getBaseUrl(env);
    }

    // ── Page Methods ───────────────────────────────────────────

    /**
     * Mở trang đăng nhập, URL đọc từ config.properties.
     */
    public LoginPage open() {
        driver.get(baseUrl);
        waitForPageLoad();
        return this;
    }

    /**
     * Đăng nhập thành công → trả về InventoryPage (Fluent).
     *
     * @param username tên đăng nhập
     * @param password mật khẩu
     * @return InventoryPage nếu login thành công
     */
    public InventoryPage login(String username, String password) {
        waitAndType(USERNAME_INPUT, username);
        waitAndType(PASSWORD_INPUT, password);
        waitAndClick(LOGIN_BUTTON);
        return new InventoryPage(driver);
    }

    /**
     * Đăng nhập với tài khoản sai → trả về LoginPage để verify lỗi (Fluent).
     *
     * @param username tên đăng nhập
     * @param password mật khẩu sai
     * @return LoginPage (vẫn ở trang login do thất bại)
     */
    public LoginPage loginExpectingFailure(String username, String password) {
        waitAndType(USERNAME_INPUT, username);
        waitAndType(PASSWORD_INPUT, password);
        waitAndClick(LOGIN_BUTTON);
        return this; // vẫn ở LoginPage vì login fail
    }

    /**
     * Lấy text thông báo lỗi.
     *
     * @return nội dung thông báo lỗi
     */
    public String getErrorMessage() {
        return getText(ERROR_MESSAGE);
    }

    /**
     * Kiểm tra thông báo lỗi có hiển thị không.
     *
     * @return true nếu có error message
     */
    public boolean isErrorDisplayed() {
        return isElementVisible(ERROR_MESSAGE);
    }
}
