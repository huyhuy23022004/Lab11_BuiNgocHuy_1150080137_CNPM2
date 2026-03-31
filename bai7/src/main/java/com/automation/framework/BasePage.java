package com.automation.framework;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * <h2>BasePage - Sửa lỗi flakiness trên CI bằng cách tăng timeout</h2>
 */
public class BasePage {

    protected WebDriver driver;
    // Tăng từ 15 lên 30 để phục vụ CI chậm
    private static final int DEFAULT_TIMEOUT = 30;
    private static final int PAGE_LOAD_TIMEOUT = 60;

    public BasePage(WebDriver driver) {
        this.driver = driver;
    }

    private WebDriverWait getWait() {
        return new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
    }

    private WebDriverWait getWait(int timeoutSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
    }

    public void waitAndClick(By locator) {
        WebElement element = getWait().until(ExpectedConditions.elementToBeClickable(locator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        element.click();
    }

    public void waitAndType(By locator, String text) {
        WebElement element = getWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.clear();
        element.sendKeys(text);
    }

    public String getText(By locator) {
        WebElement element = getWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
        return element.getText().trim();
    }

    public boolean isElementVisible(By locator) {
        try {
            // Tăng hời gian chờ kiểm tra element visible lên 10s cho CI
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement element = shortWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void scrollToElement(By locator) {
        WebElement element = getWait().until(ExpectedConditions.presenceOfElementLocated(locator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
    }

    public void waitForPageLoad() {
        getWait(PAGE_LOAD_TIMEOUT).until(
                webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete")
        );
    }

    public String getAttribute(By locator, String attributeName) {
        WebElement element = getWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
        return element.getAttribute(attributeName);
    }
}
