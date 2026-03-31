package com.lab9.bai2;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * TestDataReader - Đọc dữ liệu test từ file JSON bằng Jackson.
 */
public class TestDataReader {

    private static final ObjectMapper mapper = new ObjectMapper();

    private TestDataReader() {
    }

    public static List<Map<String, String>> readJson(String path) {
        try (InputStream is = TestDataReader.class.getClassLoader().getResourceAsStream(path)) {
            if (is == null)
                throw new RuntimeException("[TestDataReader] Không tìm thấy: " + path);
            return mapper.readValue(is, new TypeReference<List<Map<String, String>>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("[TestDataReader] Lỗi đọc JSON [" + path + "]: " + e.getMessage(), e);
        }
    }

    public static Map<String, String> getTestCase(String path, String testCaseName) {
        return readJson(path).stream()
                .filter(row -> testCaseName.equals(row.get("testCase")))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("[TestDataReader] Không tìm thấy testCase: " + testCaseName));
    }
}
