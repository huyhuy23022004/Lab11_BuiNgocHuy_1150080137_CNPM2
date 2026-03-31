package com.lab9.bai2;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * <h2>InventoryPage - Page Object cho trang danh sách sản phẩm SauceDemo</h2>
 *
 * <h3>Fluent Interface:</h3>
 * 
 * <pre>{@code
 * // Chuỗi hoàn chỉnh từ login đến giỏ hàng:
 * CartPage cart = loginPage.open()
 *         .login("standard_user", "secret_sauce")
 *         .addFirstItemToCart()
 *         .addItemByName("Sauce Labs Bike Light")
 *         .goToCart();
 * }</pre>
 */
public class InventoryPage extends BasePage {

    // ── Locators ──────────────────────────────────────────────
    /** Danh sách sản phẩm — dùng để verify trang đã load */
    private static final By INVENTORY_LIST = By.className("inventory_list");

    /** Badge số lượng trên icon giỏ hàng */
    private static final By CART_BADGE = By.className("shopping_cart_badge");

    /** Icon giỏ hàng (dùng để navigate sang CartPage) */
    private static final By CART_ICON = By.id("shopping_cart_container");

    /** Tất cả nút "Add to cart" */
    private static final By ADD_TO_CART_BTNS = By.cssSelector("[data-test^='add-to-cart']");

    /** Tất cả tên sản phẩm */
    private static final By ITEM_NAMES = By.className("inventory_item_name");

    /** URL path sau khi login thành công */
    private static final String INVENTORY_URL_FRAGMENT = "inventory";

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    // ── Page Methods ───────────────────────────────────────────

    /**
     * Kiểm tra trang Inventory đã load đầy đủ chưa.
     * Dựa vào URL chứa "inventory" và danh sách sản phẩm hiển thị.
     *
     * @return true nếu trang đã load xong
     */
    public boolean isLoaded() {
        boolean urlOk = driver.getCurrentUrl().contains(INVENTORY_URL_FRAGMENT);
        boolean listOk = isElementVisible(INVENTORY_LIST);
        return urlOk && listOk;
    }

    /**
     * Thêm sản phẩm đầu tiên trong danh sách vào giỏ hàng.
     *
     * @return InventoryPage (Fluent — vẫn ở trang này sau khi add)
     */
    public InventoryPage addFirstItemToCart() {
        // Chờ tất cả nút Add to cart hiển thị, click nút đầu tiên
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.presenceOfAllElementsLocatedBy(ADD_TO_CART_BTNS))
                .get(0)
                .click();
        return this;
    }

    /**
     * Thêm sản phẩm theo tên vào giỏ hàng.
     * Tìm sản phẩm có tên khớp rồi click nút "Add to cart" tương ứng.
     *
     * @param name tên sản phẩm cần thêm (phân biệt hoa thường)
     * @return InventoryPage (Fluent)
     * @throws IllegalArgumentException nếu không tìm thấy sản phẩm
     */
    public InventoryPage addItemByName(String name) {
        // Dùng contains(@class) để tránh lỗi khi class có nhiều giá trị
        // Tìm nút Add to cart trong cùng inventory_item chứa tên sản phẩm
        By addBtnByName = By.xpath(
                "//div[contains(@class,'inventory_item')]"
                        + "[.//div[contains(@class,'inventory_item_name') and normalize-space(text())='" + name + "']]"
                        + "//button[contains(@data-test,'add-to-cart')]");
        waitAndClick(addBtnByName);
        return this;
    }

    /**
     * Lấy số lượng item trong giỏ hàng từ badge trên icon cart.
     * Trả về 0 nếu badge không hiển thị (giỏ trống).
     *
     * @return số lượng item trong giỏ hàng (0 nếu giỏ trống)
     */
    public int getCartItemCount() {
        if (!isElementVisible(CART_BADGE)) {
            return 0;
        }
        try {
            return Integer.parseInt(getText(CART_BADGE));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Chuyển sang trang giỏ hàng bằng cách click icon Cart.
     *
     * @return CartPage (Fluent — kết thúc chuỗi fluent ở đây hoặc tiếp tục)
     */
    public CartPage goToCart() {
        waitAndClick(CART_ICON);
        return new CartPage(driver);
    }

    /**
     * Lấy danh sách tên tất cả sản phẩm đang hiển thị trên trang.
     *
     * @return List tên sản phẩm
     */
    public List<String> getAllItemNames() {
        List<WebElement> elements = new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.presenceOfAllElementsLocatedBy(ITEM_NAMES));
        return elements.stream()
                .map(WebElement::getText)
                .collect(java.util.stream.Collectors.toList());
    }
}
