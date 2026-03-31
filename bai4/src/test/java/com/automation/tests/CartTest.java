package com.automation.tests;

import com.automation.framework.ConfigReader;
import com.automation.pages.LoginPage;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CartTest extends BaseTest {

    private void loginAsStandardUser() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.openLoginPage().login(ConfigReader.getAppUsername(), ConfigReader.getAppPassword());
    }

    @Test(description = "TC06 - Thêm sản phẩm vào giỏ hàng")
    public void testAddToCart() {
        loginAsStandardUser();
        // Click "Add to Cart" cho sản phẩm đầu tiên
        getDriver().findElement(By.id("add-to-cart-sauce-labs-backpack")).click();
        String badge = getDriver().findElement(By.className("shopping_cart_badge")).getText();
        Assert.assertEquals(badge, "1", "Giỏ hàng phải hiển thị số 1");
    }

    @Test(description = "TC07 - Xóa sản phẩm khỏi giỏ hàng")
    public void testRemoveFromCart() {
        loginAsStandardUser();
        getDriver().findElement(By.id("add-to-cart-sauce-labs-backpack")).click();
        getDriver().findElement(By.id("remove-sauce-labs-backpack")).click();
        
        boolean badgeExists = true;
        try {
            badgeExists = getDriver().findElement(By.className("shopping_cart_badge")).isDisplayed();
        } catch (Exception e) {
            badgeExists = false;
        }
        Assert.assertFalse(badgeExists, "Badge không được hiển thị sau khi xóa");
    }

    @Test(description = "TC08 - Kiểm tra giỏ hàng rỗng")
    public void testEmptyCartCheckout() {
        loginAsStandardUser();
        getDriver().findElement(By.className("shopping_cart_link")).click();
        // Verify URL contains 'cart.html'
        Assert.assertTrue(getDriver().getCurrentUrl().contains("cart.html"));
    }
}
