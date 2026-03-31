package com.automation.pages;

import com.automation.framework.BasePage;
import com.automation.framework.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * <h2>LoginPage - Page Object cho trang đăng nhập SauceDemo</h2>
 *
 * <p>Minh họa cách sử dụng BasePage trong một Page Object thực tế.
 * Tất cả locator và thao tác liên quan đến Login đều được đặt ở đây.</p>
 *
 * @author Lab11 - CI/CD Framework
 * @version 1.0
 */
public class LoginPage extends BasePage {

    // =========================================================
    // LOCATORS
    // =========================================================
    private static final By USERNAME_INPUT = By.id("user-name");
    private static final By PASSWORD_INPUT = By.id("password");
    private static final By LOGIN_BUTTON = By.id("login-button");
    private static final By ERROR_MESSAGE = By.cssSelector("[data-test='error']");
    private static final By PRODUCT_TITLE = By.cssSelector(".title");

    private final String baseUrl;

    public LoginPage(WebDriver driver) {
        super(driver);
        this.baseUrl = ConfigReader.getBaseUrl("dev");
    }

    public LoginPage(WebDriver driver, String env) {
        super(driver);
        this.baseUrl = ConfigReader.getBaseUrl(env);
    }

    // =========================================================
    // PAGE METHODS
    // =========================================================

    public LoginPage openLoginPage() {
        driver.get(baseUrl);
        waitForPageLoad();
        return this;
    }

    public LoginPage login(String username, String password) {
        waitAndType(USERNAME_INPUT, username);
        waitAndType(PASSWORD_INPUT, password);
        waitAndClick(LOGIN_BUTTON);
        return this;
    }

    public String getErrorMessage() {
        return getText(ERROR_MESSAGE);
    }

    public boolean isLoginSuccessful() {
        return isElementVisible(PRODUCT_TITLE);
    }

    public boolean isErrorDisplayed() {
        return isElementVisible(ERROR_MESSAGE);
    }

    public String getUsernamePlaceholder() {
        return getAttribute(USERNAME_INPUT, "placeholder");
    }
}
