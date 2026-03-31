package com.lab9.bai2;

import org.openqa.selenium.WebDriver;

/**
 * CheckoutPage - Page Object cho trang Checkout SauceDemo.
 * Stub cơ bản — được trả về từ CartPage.goToCheckout().
 */
public class CheckoutPage extends BasePage {

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    /** Kiểm tra trang Checkout đã load (URL chứa "checkout"). */
    public boolean isLoaded() {
        return driver.getCurrentUrl().contains("checkout");
    }
}
