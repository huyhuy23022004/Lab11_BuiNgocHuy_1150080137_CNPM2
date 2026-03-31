package com.lab9.bai6;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Lớp Test mô phỏng tình huống Flaky (Test lúc đúng lúc sai)
 */
public class FlakySimulationTest extends BaseTest {

    // Biến global tĩnh đếm số lần Method được TestNG gọi Test
    private static int callCount = 0;

    @Test(description = "Test mô phỏng flaky - fail 2 lần đầu, pass lần thứ 3")
    public void testFlakyScenario() {
        callCount++;
        System.out.println("==> [FlakyTest] Đang chạy lần thứ: " + callCount);

        // Mô phỏng: 2 lần đầu tiên CỐ TÌNH đánh fail (ví dụ do lỗi đứt mạng tạm thời,
        // không tìm thấy element...)
        if (callCount <= 2) {
            String errorMsg = "Mô phỏng lỗi mạng tạm thời – lần " + callCount;
            System.err.println("=> FAIL CỐ Ý TẠI LẦN TRUY CẬP " + callCount);
            Assert.fail(errorMsg);
        }

        // Lần thứ 3 trở đi mới báo test Pass
        System.out.println("=> THÀNH CÔNG: Pass hoàn toàn tại Lần Truy Cập " + callCount);
        Assert.assertTrue(true, "Test pass ở lần thứ " + callCount);
    }
}
