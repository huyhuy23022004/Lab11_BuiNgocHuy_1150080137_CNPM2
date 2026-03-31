package com.lab9.bai4;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CheckoutPage extends BasePage {

    // Login -> Add to cart trực tiếp -> Chuyển hướng Checkout Step 1
    // (Bỏ qua qua nhiều lớp cho bài tập này đỡ dài).

    private static final By FIRST_NAME_INPUT = By.id("first-name");
    private static final By LAST_NAME_INPUT = By.id("last-name");
    private static final By ZIP_POSTAL_CODE_INPUT = By.id("postal-code");
    private static final By CONTINUE_BUTTON = By.id("continue");

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    // Hàm rút gọn cho Login và Điều hướng trực tiếp đến giỏ hàng thanh toán
    public CheckoutPage goToCheckoutDirectly() {
        driver.get("https://www.saucedemo.com/checkout-step-one.html");
        return this;
    }

    public void fillCheckoutForm(String fn, String ln, String zip) {
        waitAndType(FIRST_NAME_INPUT, fn);
        waitAndType(LAST_NAME_INPUT, ln);
        waitAndType(ZIP_POSTAL_CODE_INPUT, zip);
        waitAndClick(CONTINUE_BUTTON);
    }
}
