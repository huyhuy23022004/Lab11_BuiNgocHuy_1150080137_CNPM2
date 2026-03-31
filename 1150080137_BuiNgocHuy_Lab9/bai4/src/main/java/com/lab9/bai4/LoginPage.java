package com.lab9.bai4;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private static final By USERNAME_INPUT = By.id("user-name");
    private static final By PASSWORD_INPUT = By.id("password");
    private static final By LOGIN_BUTTON = By.id("login-button");
    private static final By ERROR_MESSAGE = By.cssSelector("[data-test='error']");

    private final String baseUrl = "https://www.saucedemo.com/";

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open() {
        driver.get(baseUrl);
        waitForPageLoad();
        return this;
    }

    public void login(String username, String password) {
        waitAndType(USERNAME_INPUT, username);
        waitAndType(PASSWORD_INPUT, password);
        waitAndClick(LOGIN_BUTTON);
    }

    public boolean isErrorDisplayed() {
        return isElementVisible(ERROR_MESSAGE);
    }
}
