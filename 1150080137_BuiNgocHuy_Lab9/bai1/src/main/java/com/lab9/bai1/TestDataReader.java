package com.lab9.bai1;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * <h2>TestDataReader - Đọc dữ liệu test từ file JSON</h2>
 *
 * <p>
 * Dùng Jackson ObjectMapper để parse file JSON trong
 * {@code src/test/resources/testdata/}
 * thành List của Map, hoặc có thể mở rộng thành POJO theo nhu cầu.
 * </p>
 *
 * <h3>Cách dùng:</h3>
 * 
 * <pre>{@code
 * List<Map<String, String>> rows = TestDataReader.readJson("testdata/login_data.json");
 * String username = rows.get(0).get("username");
 * String password = rows.get(0).get("password");
 * }</pre>
 *
 * @author Lab9 - Page Object Model Framework
 * @version 1.0
 */
public class TestDataReader {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Không cho khởi tạo instance
    private TestDataReader() {
    }

    /**
     * Đọc file JSON từ classpath và trả về danh sách các bản ghi dưới dạng Map.
     *
     * <p>
     * File JSON phải có cấu trúc mảng object, ví dụ:
     * </p>
     * 
     * <pre>{@code
     * [
     *   { "username": "user1", "password": "pass1" },
     *   { "username": "user2", "password": "pass2" }
     * ]
     * }</pre>
     *
     * @param relativePath đường dẫn tương đối từ classpath, vd:
     *                     "testdata/login_data.json"
     * @return danh sách Map String→String tương ứng với từng JSON object
     * @throws RuntimeException nếu không tìm thấy file hoặc lỗi parse JSON
     */
    public static List<Map<String, String>> readJson(String relativePath) {
        try (InputStream is = TestDataReader.class
                .getClassLoader()
                .getResourceAsStream(relativePath)) {

            if (is == null) {
                throw new RuntimeException("[TestDataReader] Không tìm thấy file: " + relativePath);
            }

            return objectMapper.readValue(is, new TypeReference<List<Map<String, String>>>() {
            });

        } catch (Exception e) {
            throw new RuntimeException("[TestDataReader] Lỗi đọc JSON [" + relativePath + "]: " + e.getMessage(), e);
        }
    }

    /**
     * Lấy dữ liệu của một test case cụ thể theo tên testCase.
     *
     * @param relativePath đường dẫn file JSON
     * @param testCaseName giá trị của field "testCase" cần tìm
     * @return Map data của test case đó, hoặc null nếu không tìm thấy
     */
    public static Map<String, String> getTestCase(String relativePath, String testCaseName) {
        return readJson(relativePath).stream()
                .filter(row -> testCaseName.equals(row.get("testCase")))
                .findFirst()
                .orElse(null);
    }
}
