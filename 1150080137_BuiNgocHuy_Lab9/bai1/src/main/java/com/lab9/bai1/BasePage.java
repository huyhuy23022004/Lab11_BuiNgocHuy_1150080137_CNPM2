package com.lab9.bai1;

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
 * <p><b>Nguyên tắc thiết kế:</b></p>
 * <ul>
 *   <li>KHÔNG dùng Thread.sleep() - chỉ dùng WebDriverWait (Explicit Wait)</li>
 *   <li>Xử lý StaleElementReferenceException để tránh crash khi DOM render lại</li>
 *   <li>Tất cả method đều nhận {@link By} locator thay vì {@link WebElement}
 *       để tránh lỗi stale element</li>
 * </ul>
 *
 * @author Lab9 - Page Object Model Framework
 * @version 1.0
 */
public class BasePage {

    /** WebDriver instance được truyền từ BaseTest */
    protected WebDriver driver;

    /** Thời gian chờ mặc định cho tất cả các Explicit Wait (giây) */
    private static final int DEFAULT_TIMEOUT = 15;

    /** Thời gian chờ tải trang hoàn toàn (giây) */
    private static final int PAGE_LOAD_TIMEOUT = 30;

    /**
     * Khởi tạo BasePage với WebDriver đã được cấu hình.
     *
     * @param driver WebDriver instance từ BaseTest (ThreadLocal-safe)
     */
    public BasePage(WebDriver driver) {
        this.driver = driver;
    }

    // =========================================================
    // HELPER PRIVATE METHOD
    // =========================================================

    /**
     * Tạo WebDriverWait với timeout mặc định.
     *
     * @return WebDriverWait instance
     */
    private WebDriverWait getWait() {
        return new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
    }

    /**
     * Tạo WebDriverWait với timeout tùy chỉnh.
     *
     * @param timeoutSeconds thời gian chờ tối đa (giây)
     * @return WebDriverWait instance
     */
    private WebDriverWait getWait(int timeoutSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
    }

    // =========================================================
    // 7 CORE METHODS
    // =========================================================

    /**
     * <b>Method 1: waitAndClick</b>
     *
     * <p>Chờ element xuất hiện và có thể click được, sau đó thực hiện click.
     * Tự động cuộn element vào vùng nhìn thấy trước khi click.</p>
     *
     * <p><b>Trường hợp dùng:</b> Click button, link, checkbox, radio button</p>
     *
     * <p><b>Ví dụ:</b></p>
     * <pre>{@code
     * waitAndClick(By.id("loginBtn"));
     * waitAndClick(By.xpath("//button[text()='Submit']"));
     * }</pre>
     *
     * @param locator định vị element cần click
     * @throws TimeoutException nếu element không xuất hiện sau {@value DEFAULT_TIMEOUT} giây
     */
    public void waitAndClick(By locator) {
        WebElement element = getWait().until(
                ExpectedConditions.elementToBeClickable(locator)
        );
        // Cuộn vào vùng nhìn thấy để tránh lỗi "Element not clickable at point"
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        element.click();
    }

    /**
     * <b>Method 2: waitAndType</b>
     *
     * <p>Chờ element input xuất hiện, xóa nội dung cũ, sau đó nhập text mới.
     * Tự động clear trước khi gõ để tránh text bị nối vào nội dung cũ.</p>
     *
     * <p><b>Trường hợp dùng:</b> Nhập liệu vào ô input, textarea, search box</p>
     *
     * <p><b>Ví dụ:</b></p>
     * <pre>{@code
     * waitAndType(By.id("username"), "standard_user");
     * waitAndType(By.name("password"), "secret_sauce");
     * }</pre>
     *
     * @param locator định vị element input
     * @param text    nội dung cần nhập
     * @throws TimeoutException nếu element không xuất hiện sau {@value DEFAULT_TIMEOUT} giây
     */
    public void waitAndType(By locator, String text) {
        WebElement element = getWait().until(
                ExpectedConditions.visibilityOfElementLocated(locator)
        );
        element.clear();
        element.sendKeys(text);
    }

    /**
     * <b>Method 3: getText</b>
     *
     * <p>Chờ element hiển thị và lấy nội dung text bên trong.
     * Tự động trim khoảng trắng thừa ở đầu và cuối.</p>
     *
     * <p><b>Trường hợp dùng:</b> Lấy text từ label, heading, thông báo lỗi,
     * nội dung sản phẩm để dùng trong assertion</p>
     *
     * <p><b>Ví dụ:</b></p>
     * <pre>{@code
     * String errorMsg = getText(By.cssSelector(".error-message"));
     * Assert.assertEquals(errorMsg, "Username and password do not match");
     * }</pre>
     *
     * @param locator định vị element cần lấy text
     * @return chuỗi text của element (đã trim), hoặc chuỗi rỗng nếu không có text
     * @throws TimeoutException nếu element không hiển thị sau {@value DEFAULT_TIMEOUT} giây
     */
    public String getText(By locator) {
        WebElement element = getWait().until(
                ExpectedConditions.visibilityOfElementLocated(locator)
        );
        return element.getText().trim();
    }

    /**
     * <b>Method 4: isElementVisible</b>
     *
     * <p>Kiểm tra element có hiển thị trên màn hình hay không.
     * Xử lý {@link StaleElementReferenceException} — thực tế hay gặp khi trang
     * render lại DOM (ví dụ: sau khi AJAX cập nhật, hoặc React/Vue re-render component).</p>
     *
     * <p><b>Cơ chế xử lý StaleElementReferenceException:</b> Khi DOM bị rebuild,
     * element reference cũ sẽ bị "stale". Method này bắt exception và trả về
     * {@code false} thay vì crash test.</p>
     *
     * <p><b>Trường hợp dùng:</b> Kiểm tra element có tồn tại trước khi thao tác,
     * verify trạng thái hiển thị/ẩn của toast notification, modal, spinner</p>
     *
     * <p><b>Ví dụ:</b></p>
     * <pre>{@code
     * if (isElementVisible(By.id("loadingSpinner"))) {
     *     waitForPageLoad(); // chờ spinner biến mất
     * }
     * Assert.assertTrue(isElementVisible(By.cssSelector(".success-message")));
     * }</pre>
     *
     * @param locator định vị element cần kiểm tra
     * @return {@code true} nếu element hiển thị, {@code false} nếu không tìm thấy
     *         hoặc bị ẩn hoặc element bị stale
     */
    public boolean isElementVisible(By locator) {
        try {
            // Dùng wait ngắn hơn để không làm test chạy chậm
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement element = shortWait.until(
                    ExpectedConditions.visibilityOfElementLocated(locator)
            );
            return element.isDisplayed();
        } catch (StaleElementReferenceException e) {
            // DOM bị render lại khiến reference cũ không còn hợp lệ
            // Thử lại một lần để xử lý trường hợp thoáng qua
            try {
                return driver.findElement(locator).isDisplayed();
            } catch (Exception retryEx) {
                return false;
            }
        } catch (TimeoutException | NoSuchElementException e) {
            return false;
        }
    }

    /**
     * <b>Method 5: scrollToElement</b>
     *
     * <p>Cuộn trang để đưa element vào giữa vùng nhìn thấy của màn hình.
     * Sử dụng JavaScript để đảm bảo hoạt động ngay cả khi element bị che khuất.</p>
     *
     * <p><b>Trường hợp dùng:</b> Cuộn đến element ở cuối trang trước khi click,
     * cuộn đến section cụ thể để screenshot, xử lý sticky header che mất element</p>
     *
     * <p><b>Ví dụ:</b></p>
     * <pre>{@code
     * scrollToElement(By.id("footer-subscribe-btn"));
     * waitAndClick(By.id("footer-subscribe-btn"));
     * }</pre>
     *
     * @param locator định vị element cần cuộn đến
     * @throws TimeoutException nếu element không tồn tại sau {@value DEFAULT_TIMEOUT} giây
     */
    public void scrollToElement(By locator) {
        WebElement element = getWait().until(
                ExpectedConditions.presenceOfElementLocated(locator)
        );
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});",
                element
        );
        // Chờ ngắn để animation scroll hoàn tất (không dùng Thread.sleep)
        try {
            new WebDriverWait(driver, Duration.ofMillis(500))
                    .until(ExpectedConditions.visibilityOf(element));
        } catch (Exception ignored) {
            // Element đã visible hoặc không cần chờ thêm
        }
    }

    /**
     * <b>Method 6: waitForPageLoad</b>
     *
     * <p>Chờ trang tải hoàn toàn bằng cách kiểm tra {@code document.readyState == "complete"}.
     * Đảm bảo tất cả tài nguyên (HTML, CSS, JS, ảnh) đã được tải xong.</p>
     *
     * <p><b>Khi nào dùng:</b></p>
     * <ul>
     *   <li>Sau khi navigate sang trang mới</li>
     *   <li>Sau khi submit form có redirect</li>
     *   <li>Trước khi thực hiện action đầu tiên trên trang mới</li>
     * </ul>
     *
     * <p><b>Ví dụ:</b></p>
     * <pre>{@code
     * driver.get("https://saucedemo.com");
     * waitForPageLoad();
     * waitAndType(By.id("user-name"), "standard_user");
     * }</pre>
     *
     * @throws TimeoutException nếu trang chưa tải xong sau {@value PAGE_LOAD_TIMEOUT} giây
     */
    public void waitForPageLoad() {
        getWait(PAGE_LOAD_TIMEOUT).until(
                webDriver -> ((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState")
                        .equals("complete")
        );
    }

    /**
     * <b>Method 7: getAttribute</b>
     *
     * <p>Chờ element hiển thị và lấy giá trị của một attribute HTML cụ thể.
     * Hữu ích để lấy giá trị các attribute như {@code value}, {@code href},
     * {@code src}, {@code class}, {@code placeholder}.</p>
     *
     * <p><b>Trường hợp dùng:</b></p>
     * <ul>
     *   <li>Lấy {@code value} của input để verify nội dung đã nhập</li>
     *   <li>Lấy {@code href} của link để kiểm tra URL</li>
     *   <li>Lấy {@code class} để kiểm tra trạng thái active/disabled</li>
     *   <li>Lấy {@code src} của ảnh để verify ảnh đúng</li>
     * </ul>
     *
     * <p><b>Ví dụ:</b></p>
     * <pre>{@code
     * String inputValue = getAttribute(By.id("username"), "value");
     * String linkUrl    = getAttribute(By.linkText("Home"), "href");
     * String btnClass   = getAttribute(By.id("submitBtn"), "class");
     * }</pre>
     *
     * @param locator       định vị element cần lấy attribute
     * @param attributeName tên attribute HTML cần lấy (vd: "value", "href", "class")
     * @return giá trị của attribute, hoặc {@code null} nếu attribute không tồn tại
     * @throws TimeoutException nếu element không hiển thị sau {@value DEFAULT_TIMEOUT} giây
     */
    public String getAttribute(By locator, String attributeName) {
        WebElement element = getWait().until(
                ExpectedConditions.visibilityOfElementLocated(locator)
        );
        return element.getAttribute(attributeName);
    }
}
