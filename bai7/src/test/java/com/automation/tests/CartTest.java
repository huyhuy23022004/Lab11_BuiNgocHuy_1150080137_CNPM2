package com.automation.tests;

import com.automation.framework.ConfigReader;
import com.automation.pages.LoginPage;
import io.qameta.allure.*;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

@Feature("Quản lý Giỏ hàng")
public class CartTest extends BaseTest {

    private void loginAsStandardUser() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.openLoginPage().login(ConfigReader.getAppUsername(), ConfigReader.getAppPassword());
    }

    @Test(description = "TC06 - Thêm sản phẩm vào giỏ hàng")
    @Story("Thêm sản phẩm")
    @Severity(SeverityLevel.CRITICAL)
    public void testAddToCart() {
        Allure.step("Đăng nhập vào hệ thống", this::loginAsStandardUser);
        Allure.step("Click 'Add to Cart' sản phẩm Backpack", () -> {
            getDriver().findElement(By.id("add-to-cart-sauce-labs-backpack")).click();
        });
        Allure.step("Kiểm tra badge giỏ hàng hiển thị số 1", () -> {
            String badge = getDriver().findElement(By.className("shopping_cart_badge")).getText();
            Assert.assertEquals(badge, "1");
        });
    }

    @Test(description = "TC07 - Xóa sản phẩm khỏi giỏ hàng")
    @Story("Xóa sản phẩm")
    @Severity(SeverityLevel.NORMAL)
    public void testRemoveFromCart() {
        Allure.step("Đăng nhập vào hệ thống", this::loginAsStandardUser);
        Allure.step("Thêm sản phẩm vào giỏ hàng", () -> {
             getDriver().findElement(By.id("add-to-cart-sauce-labs-backpack")).click();
        });
        Allure.step("Click 'Remove' sản phẩm", () -> {
             getDriver().findElement(By.id("remove-sauce-labs-backpack")).click();
        });
        Allure.step("Kiểm tra badge giỏ hàng không còn hiển thị", () -> {
            boolean badgeExists = true;
            try {
                badgeExists = getDriver().findElement(By.className("shopping_cart_badge")).isDisplayed();
            } catch (Exception e) {
                badgeExists = false;
            }
            Assert.assertFalse(badgeExists);
        });
    }
}
