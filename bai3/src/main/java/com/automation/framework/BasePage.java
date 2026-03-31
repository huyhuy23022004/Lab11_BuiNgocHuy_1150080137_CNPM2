package com.automation.framework;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * <h2>BasePage - Lớp nền tảng cho tất cả Page Object</h2>
 *
 * <p>Mọi Page Object trong framework đều kế thừa từ lớp này.
 * BasePage cung cấp các method tiện ích dùng chung với Explicit Wait tích hợp sẵn,
 * giúp code test ổn định và không cần dùng Thread.sleep().</p>
 *
 * @author Lab11 - CI/CD Framework
 * @version 1.0
 */
public class BasePage {

    protected WebDriver driver;
    private static final int DEFAULT_TIMEOUT = 15;
    private static final int PAGE_LOAD_TIMEOUT = 30;

    public BasePage(WebDriver driver) {
        this.driver = driver;
    }

    private WebDriverWait getWait() {
        return new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
    }

    private WebDriverWait getWait(int timeoutSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
    }

    // =========================================================
    // 7 CORE METHODS (kế thừa từ Lab 9)
    // =========================================================

    public void waitAndClick(By locator) {
        WebElement element = getWait().until(
                ExpectedConditions.elementToBeClickable(locator)
        );
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        element.click();
    }

    public void waitAndType(By locator, String text) {
        WebElement element = getWait().until(
                ExpectedConditions.visibilityOfElementLocated(locator)
        );
        element.clear();
        element.sendKeys(text);
    }

    public String getText(By locator) {
        WebElement element = getWait().until(
                ExpectedConditions.visibilityOfElementLocated(locator)
        );
        return element.getText().trim();
    }

    public boolean isElementVisible(By locator) {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement element = shortWait.until(
                    ExpectedConditions.visibilityOfElementLocated(locator)
            );
            return element.isDisplayed();
        } catch (StaleElementReferenceException e) {
            try {
                return driver.findElement(locator).isDisplayed();
            } catch (Exception retryEx) {
                return false;
            }
        } catch (TimeoutException | NoSuchElementException e) {
            return false;
        }
    }

    public void scrollToElement(By locator) {
        WebElement element = getWait().until(
                ExpectedConditions.presenceOfElementLocated(locator)
        );
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});",
                element
        );
        try {
            new WebDriverWait(driver, Duration.ofMillis(500))
                    .until(ExpectedConditions.visibilityOf(element));
        } catch (Exception ignored) {
        }
    }

    public void waitForPageLoad() {
        getWait(PAGE_LOAD_TIMEOUT).until(
                webDriver -> ((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState")
                        .equals("complete")
        );
    }

    public String getAttribute(By locator, String attributeName) {
        WebElement element = getWait().until(
                ExpectedConditions.visibilityOfElementLocated(locator)
        );
        return element.getAttribute(attributeName);
    }
}
