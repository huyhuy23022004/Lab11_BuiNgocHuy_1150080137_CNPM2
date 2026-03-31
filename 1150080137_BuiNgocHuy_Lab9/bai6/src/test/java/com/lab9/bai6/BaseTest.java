package com.lab9.bai6;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTest {

    @BeforeMethod
    public void setUp() {
        System.out.println("\n[BaseTest] Chuẩn bị môi trường Khởi Chạy Test...");
    }

    @AfterMethod
    public void tearDown() {
        System.out.println("[BaseTest] Hoàn tất Cleanup Test Engine.\n");
    }
}
