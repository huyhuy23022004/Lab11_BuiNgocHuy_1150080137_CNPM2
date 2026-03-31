package com.lab9.bai3;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private static final By USERNAME_INPUT = By.id("user-name");
    private static final By PASSWORD_INPUT = By.id("password");
    private static final By LOGIN_BUTTON = By.id("login-button");
    private static final By ERROR_MESSAGE = By.cssSelector("[data-test='error']");

    private final String baseUrl;

    public LoginPage(WebDriver driver) {
        super(driver);
        this.baseUrl = ConfigReader.getBaseUrl();
    }

    public LoginPage open() {
        driver.get(baseUrl);
        waitForPageLoad();
        return this;
    }

    // Thực hiện đăng nhập, không trả về trang Inventory cụ thể cho Data-Driven
    public void login(String username, String password) {
        waitAndType(USERNAME_INPUT, username);
        waitAndType(PASSWORD_INPUT, password);
        waitAndClick(LOGIN_BUTTON);
    }

    public String getErrorMessage() {
        return getText(ERROR_MESSAGE);
    }

    public boolean isErrorDisplayed() {
        return isElementVisible(ERROR_MESSAGE);
    }
}
