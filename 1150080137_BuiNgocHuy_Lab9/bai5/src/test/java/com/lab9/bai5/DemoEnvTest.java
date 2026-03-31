package com.lab9.bai5;

import org.testng.Assert;
import org.testng.annotations.Test;

public class DemoEnvTest extends BaseTest {

    @Test
    public void testEnvironmentConfiguration() {
        ConfigReader config = ConfigReader.getInstance();

        // Không hardcode ở bất kì đâu ngoài File config
        String expectedUrl = config.getBaseUrl();

        System.out.println("===> URL TỪ CONFIG LÀ: " + expectedUrl);

        // Xác minh tự động chỉ qua string tránh lỗi Chrome DNS Error khi load staging
        // giả
        Assert.assertNotNull(expectedUrl, "Test failed! Không lấy được URL từ Config");
        Assert.assertTrue(expectedUrl.contains("saucedemo"), "Lỗi: URL không chứa domain đúng");
    }
}
