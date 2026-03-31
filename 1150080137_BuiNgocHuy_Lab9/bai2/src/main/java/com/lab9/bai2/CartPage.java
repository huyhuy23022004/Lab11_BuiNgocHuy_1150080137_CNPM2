package com.lab9.bai2;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <h2>CartPage - Page Object cho trang giỏ hàng SauceDemo</h2>
 *
 * <h3>Đặc biệt:</h3>
 * <ul>
 * <li>{@link #getItemCount()} trả về 0 khi giỏ trống — KHÔNG throw
 * exception</li>
 * <li>{@link #getItemNames()} trả về danh sách rỗng khi giỏ trống</li>
 * </ul>
 *
 * <h3>Fluent Interface:</h3>
 * 
 * <pre>{@code
 * int count = cartPage.getItemCount();
 * cartPage.removeFirstItem().getItemCount(); // sau chuỗi remove
 * CheckoutPage checkout = cartPage.goToCheckout();
 * }</pre>
 */
public class CartPage extends BasePage {

    // ── Locators ──────────────────────────────────────────────
    /** Mỗi item trong giỏ hàng */
    private static final By CART_ITEMS = By.className("cart_item");

    /** Tên item trong giỏ hàng */
    private static final By ITEM_NAME_LABELS = By.className("inventory_item_name");

    /** Tất cả nút Remove trong giỏ */
    private static final By REMOVE_BUTTONS = By.cssSelector("[data-test^='remove']");

    /** Nút Checkout */
    private static final By CHECKOUT_BUTTON = By.id("checkout");

    /** Continue Shopping */
    private static final By CONTINUE_BTN = By.id("continue-shopping");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    // ── Page Methods ───────────────────────────────────────────

    /**
     * Lấy số lượng item trong giỏ hàng.
     *
     * <p>
     * <b>Xử lý giỏ trống:</b> Khi giỏ không có item, trả về 0.
     * KHÔNG throw exception — đây là yêu cầu kỹ thuật của bài.
     * </p>
     *
     * @return số item trong giỏ, 0 nếu giỏ trống
     */
    public int getItemCount() {
        try {
            List<WebElement> items = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.presenceOfAllElementsLocatedBy(CART_ITEMS));
            return items.size();
        } catch (Exception e) {
            // Giỏ trống → không có element cart_item → trả về 0 thay vì crash
            return 0;
        }
    }

    /**
     * Xóa item đầu tiên trong giỏ hàng.
     *
     * <p>
     * Nếu giỏ trống (không có nút Remove), method bỏ qua và trả về this.
     * Không throw exception.
     * </p>
     *
     * @return CartPage (Fluent — cho phép chaining)
     */
    public CartPage removeFirstItem() {
        try {
            List<WebElement> removeBtns = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.presenceOfAllElementsLocatedBy(REMOVE_BUTTONS));
            if (!removeBtns.isEmpty()) {
                removeBtns.get(0).click();
            }
        } catch (Exception e) {
            // Giỏ trống → không có nút Remove → bỏ qua
            System.out.println("[CartPage] removeFirstItem: Giỏ trống, không có item để xóa.");
        }
        return this;
    }

    /**
     * Chuyển sang trang Checkout.
     *
     * @return CheckoutPage (Fluent)
     */
    public CheckoutPage goToCheckout() {
        waitAndClick(CHECKOUT_BUTTON);
        return new CheckoutPage(driver);
    }

    /**
     * Lấy danh sách tên tất cả item trong giỏ hàng.
     *
     * <p>
     * Trả về danh sách rỗng khi giỏ trống — KHÔNG throw exception.
     * </p>
     *
     * @return List tên item, hoặc danh sách rỗng nếu giỏ trống
     */
    public List<String> getItemNames() {
        try {
            List<WebElement> labels = new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.presenceOfAllElementsLocatedBy(ITEM_NAME_LABELS));
            return labels.stream()
                    .filter(WebElement::isDisplayed) // Filter bỏ các item bị ẩn (nếu có)
                    .map(WebElement::getText)
                    .filter(text -> !text.trim().isEmpty()) // Lọc bỏ text rỗng
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // Giỏ trống → trả về danh sách rỗng
            return Collections.emptyList();
        }
    }

    /**
     * Quay lại trang Inventory để tiếp tục mua sắm.
     *
     * @return InventoryPage
     */
    public InventoryPage continueShopping() {
        waitAndClick(CONTINUE_BTN);
        return new InventoryPage(driver);
    }
}
