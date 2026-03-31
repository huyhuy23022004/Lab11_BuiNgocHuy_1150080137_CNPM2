package com.lab9.bai4;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class CheckoutTest extends BaseTest {

    /**
     * Giở thủ thuật invocationCount = 2 để TestNG chạy phương thức này 2 lần liên
     * tục.
     * Chạy mỗi lần, sẽ Console In ra được Fake Data đổi đi theo từng lần khởi tạo.
     */
    @Test(invocationCount = 2, description = "Test Data-driven ngẫu nhiên qua JavaFaker")
    public void testCheckoutWithRandomData() {
        // Lấy bộ MAP Data Faker ngẫu nhiên
        Map<String, String> formData = TestDataFactory.randomCheckoutData();

        System.out.println("\n============ KẾT QUẢ DATA FAKER ============");
        System.out.println("First Name : " + formData.get("firstName"));
        System.out.println("Last Name  : " + formData.get("lastName"));
        System.out.println("Postal Code: " + formData.get("postalCode"));
        System.out.println("============================================\n");

        // 1. Phải Login trước qua cookie (để vào dc checkout route bảo vệ)
        new LoginPage(getDriver())
                .open()
                .login("standard_user", "secret_sauce");

        // 2. Chuyển thẳng đến Form checkout rồi Nhồi Fake Data
        CheckoutPage checkoutPage = new CheckoutPage(getDriver());
        checkoutPage.goToCheckoutDirectly();
        checkoutPage.fillCheckoutForm(
                formData.get("firstName"),
                formData.get("lastName"),
                formData.get("postalCode"));

        // 3. Đo lường Thành Công chuyển qua step-two (Overview URL)
        Assert.assertTrue(getDriver().getCurrentUrl().contains("checkout-step-two"),
                "Lỗi: Không thể điền dữ liệu Faker qua Next Step");
    }
}
