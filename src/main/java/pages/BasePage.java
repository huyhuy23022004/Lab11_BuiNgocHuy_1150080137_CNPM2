package pages;

import utils.*;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * BasePage - Lớp nền tảng cho tất cả Page Object (Bài 2).
 * Cung cấp 7 method cốt lõi với Explicit Wait, không dùng Thread.sleep().
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

    private WebDriverWait getWait(int seconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(seconds));
    }

    /** Chờ có thể click rồi click, auto scroll vào giữa màn hình. */
    public void waitAndClick(By locator) {
        WebElement el = getWait().until(ExpectedConditions.elementToBeClickable(locator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", el);
        el.click();
    }

    /** Chờ visible, clear, rồi gõ text. */
    public void waitAndType(By locator, String text) {
        WebElement el = getWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
        el.clear();
        el.sendKeys(text);
    }

    /** Chờ visible và lấy text (đã trim). */
    public String getText(By locator) {
        return getWait().until(ExpectedConditions.visibilityOfElementLocated(locator))
                .getText().trim();
    }

    /**
     * Kiểm tra element có hiển thị không.
     * Xử lý StaleElementReferenceException khi DOM render lại.
     */
    public boolean isElementVisible(By locator) {
        try {
            WebElement el = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));
            return el.isDisplayed();
        } catch (StaleElementReferenceException e) {
            try {
                return driver.findElement(locator).isDisplayed();
            } catch (Exception ex) {
                return false;
            }
        } catch (TimeoutException | NoSuchElementException e) {
            return false;
        }
    }

    /** Cuộn trang để element vào giữa màn hình. */
    public void scrollToElement(By locator) {
        WebElement el = getWait().until(ExpectedConditions.presenceOfElementLocated(locator));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({behavior:'smooth',block:'center'});", el);
    }

    /** Chờ document.readyState == "complete". */
    public void waitForPageLoad() {
        getWait(PAGE_LOAD_TIMEOUT)
                .until(wd -> ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete"));
    }

    /** Chờ visible rồi lấy giá trị attribute. */
    public String getAttribute(By locator, String attr) {
        return getWait().until(ExpectedConditions.visibilityOfElementLocated(locator))
                .getAttribute(attr);
    }
}
