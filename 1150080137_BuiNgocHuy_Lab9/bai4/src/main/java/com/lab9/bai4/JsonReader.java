package com.lab9.bai4;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;

/**
 * JsonReader đọc list object từ json tự động map vào UserData ArrayList thông
 * qua Jackson.
 */
public class JsonReader {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<UserData> readUsers(String path) {
        try (InputStream is = JsonReader.class.getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                throw new RuntimeException("Không tìm thấy file json: " + path);
            }
            return mapper.readValue(is, new TypeReference<List<UserData>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("Lỗi đọc/parse file JSON: " + e.getMessage(), e);
        }
    }
}
