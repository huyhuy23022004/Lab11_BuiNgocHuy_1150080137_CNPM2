package tests;

import pages.*;
import utils.*;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

/**
 * <h2>CartTest - Kiểm thử giỏ hàng SauceDemo (Bài 2)</h2>
 *
 * <p>
 * Kế thừa BaseTest. Sử dụng chuỗi Fluent Interface đầy đủ:
 * </p>
 * 
 * <pre>{@code
 * loginPage.open()
 *         .login(u, p) // → InventoryPage
 *         .addFirstItemToCart() // → InventoryPage
 *         .goToCart() // → CartPage
 *         .getItemCount();
 * }</pre>
 *
 * <p>
 * <b>Quy tắc POM được tuân thủ:</b>
 * </p>
 * <ul>
 * <li>KHÔNG có By.id() hay driver.findElement() trong class này</li>
 * <li>Không dùng Thread.sleep()</li>
 * <li>Dùng getDriver() từ BaseTest</li>
 * </ul>
 */
public class CartTest extends BaseTest {

    private static final String LOGIN_DATA = "testdata/login_data.json";

    /**
     * TC04 - Add sản phẩm đầu tiên vào giỏ → cart badge hiển thị 1.
     * Fluent: login → addFirstItemToCart → getCartItemCount
     */
    @Test(description = "TC04 - Thêm sản phẩm đầu tiên vào giỏ hàng")
    public void testAddFirstItemToCart() {
        Map<String, String> data = TestDataReader.getTestCase(LOGIN_DATA, "TC01_LoginSuccess");

        InventoryPage inventoryPage = new LoginPage(getDriver())
                .open()
                .login(data.get("username"), data.get("password"))
                .addFirstItemToCart(); // Fluent, vẫn ở InventoryPage

        Assert.assertEquals(
                inventoryPage.getCartItemCount(), 1,
                "Sau khi add 1 sản phẩm, badge giỏ hàng phải hiển thị 1");
    }

    /**
     * TC05 - Add sản phẩm theo tên → kiểm tra tên xuất hiện trong giỏ.
     * Fluent đầy đủ: login → addItemByName → goToCart → getItemNames
     */
    @Test(description = "TC05 - Thêm sản phẩm theo tên → kiểm tra trong giỏ hàng")
    public void testAddItemByNameAndVerifyInCart() {
        Map<String, String> data = TestDataReader.getTestCase(LOGIN_DATA, "TC01_LoginSuccess");
        String targetItem = "Sauce Labs Bike Light";

        CartPage cartPage = new LoginPage(getDriver())
                .open()
                .login(data.get("username"), data.get("password"))
                .addItemByName(targetItem) // thêm đúng sản phẩm theo tên
                .goToCart(); // → CartPage

        List<String> names = cartPage.getItemNames();
        Assert.assertFalse(names.isEmpty(), "Giỏ hàng phải có ít nhất 1 sản phẩm");
        Assert.assertTrue(
                names.contains(targetItem),
                "Sản phẩm '" + targetItem + "' phải có trong giỏ. Actual: " + names);
    }

    /**
     * TC06 - Giỏ hàng trống → getItemCount() = 0, không throw exception.
     * Đây là yêu cầu kỹ thuật đặc biệt của bài.
     */
    @Test(description = "TC06 - Giỏ hàng trống → getItemCount() = 0, không crash")
    public void testCartEmptyReturnsZero() {
        Map<String, String> data = TestDataReader.getTestCase(LOGIN_DATA, "TC01_LoginSuccess");

        // Login rồi vào giỏ NGAY — không add item nào
        CartPage cartPage = new LoginPage(getDriver())
                .open()
                .login(data.get("username"), data.get("password"))
                .goToCart(); // vào giỏ khi chưa add gì

        // getItemCount() phải trả về 0, KHÔNG throw exception
        int count = cartPage.getItemCount();
        Assert.assertEquals(count, 0, "Giỏ trống phải trả về 0, không throw exception");

        // getItemNames() phải trả về list rỗng, không crash
        List<String> names = cartPage.getItemNames();
        Assert.assertTrue(names.isEmpty(), "getItemNames() khi giỏ trống phải trả về list rỗng");
    }

    /**
     * TC07 - Add 2 sản phẩm rồi remove 1 → kiểm tra còn lại 1.
     * Fluent: login → add×2 → goToCart → removeFirstItem → getItemCount
     */
    @Test(description = "TC07 - Add 2 sản phẩm, remove 1 → còn 1 trong giỏ")
    public void testRemoveItemFromCart() {
        Map<String, String> data = TestDataReader.getTestCase(LOGIN_DATA, "TC01_LoginSuccess");

        CartPage cartPage = new LoginPage(getDriver())
                .open()
                .login(data.get("username"), data.get("password"))
                .addFirstItemToCart() // add sản phẩm 1
                .addItemByName("Sauce Labs Bike Light") // add sản phẩm 2
                .goToCart() // → CartPage
                .removeFirstItem(); // xóa sản phẩm đầu tiên

        Assert.assertEquals(
                cartPage.getItemCount(), 1,
                "Sau khi remove 1 trong 2 sản phẩm, giỏ phải còn 1");
    }
}
