package com.automation.framework;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * <h2>TestDataReader - Đọc dữ liệu test từ file JSON</h2>
 *
 * @author Lab11 - CI/CD Framework
 * @version 1.0
 */
public class TestDataReader {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private TestDataReader() {
    }

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

    public static Map<String, String> getTestCase(String relativePath, String testCaseName) {
        return readJson(relativePath).stream()
                .filter(row -> testCaseName.equals(row.get("testCase")))
                .findFirst()
                .orElse(null);
    }
}
