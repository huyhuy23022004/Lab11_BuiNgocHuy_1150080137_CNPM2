package com.lab9.bai4;

import com.github.javafaker.Faker;

import java.util.Locale;
import java.util.Map;

/**
 * Tập trung tất cả logic sinh dữ liệu giả cho Test Data-Driven
 */
public class TestDataFactory {

    // Sinh fake data dùng ngữ cảnh tiếng Việt hoặc tiếng Anh tuỳ ý
    private static final Faker faker = new Faker(new Locale("vi"));

    public static String randomFirstName() {
        return faker.name().firstName();
    }

    public static String randomLastName() {
        return faker.name().lastName();
    }

    public static String randomPostalCode() {
        return faker.address().zipCode();
    }

    /**
     * @return 1 bộ Map giả lập Info điền form Checkout
     */
    public static Map<String, String> randomCheckoutData() {
        return Map.of(
                "firstName", randomFirstName(),
                "lastName", randomLastName(),
                "postalCode", randomPostalCode());
    }
}
