package com.lab9.bai3;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;

import org.testng.annotations.Test;

/**
 * MockDataGenerator sinh ra excel với toàn chỉnh cột theo đúng yêu cầu đề bài
 * (bai 3).
 * SẼ ĐƯỢC CHẠY CHỈ ĐỂ TẠO FILE LOGIN_DATA.XLSX
 */
public class MockDataGenerator {

    @Test
    public void generateExcelData() {
        long startTime = System.currentTimeMillis();

        try (Workbook workbook = new XSSFWorkbook()) {
            // 1. Sheet: SmokeCases (Tối thiểu 3 dòng: username | password | expected_url |
            // description)
            Sheet sSmoke = workbook.createSheet("SmokeCases");
            writeHeader(sSmoke, "username", "password", "expected_url", "description");
            writeRow(sSmoke, 1, "standard_user", "secret_sauce", "inventory.html", "Test 01 - Login tiêu chuẩn");
            writeRow(sSmoke, 2, "problem_user", "secret_sauce", "inventory.html", "Test 02 - Login user hay lỗi");
            writeRow(sSmoke, 3, "performance_glitch_user", "secret_sauce", "inventory.html",
                    "Test 03 - Login user tải lâu");

            // 2. Sheet: NegativeCases (Tối thiểu 5 dòng: username | password | expected
            // error | description)
            Sheet sNegative = workbook.createSheet("NegativeCases");
            writeHeader(sNegative, "username", "password", "expected_error", "description");
            writeRow(sNegative, 1, "standard_user", "wrong_pass", "Username and password do not match",
                    "N01 - Sai password tiêu chuẩn");
            writeRow(sNegative, 2, "locked_out_user", "secret_sauce", "locked out", "N02 - Tài khoản đã bị block");
            writeRow(sNegative, 3, "", "secret_sauce", "Username is required", "N03 - Username bị bỏ trống");
            writeRow(sNegative, 4, "standard_user", "", "Password is required", "N04 - Password bị bỏ trống");
            writeRow(sNegative, 5, "invalid_user", "invalid_pass", "Username and password do not match",
                    "N05 - Username và mẩu khâu sai hoàn toàn");

            // 3. Sheet: BoundaryCases (Tối thiểu 4 dòng: username | password | expected
            // error | description)
            Sheet sBoundary = workbook.createSheet("BoundaryCases");
            writeHeader(sBoundary, "username", "password", "expected_error", "description");
            // Sinh kí tự chuỗi dài
            String longString = "A".repeat(200);
            writeRow(sBoundary, 1, longString, "secret_sauce", "Username and password do not match",
                    "B01 - Username nhập quá số lượng tối đa max-lenght kí tự > 200");
            writeRow(sBoundary, 2, "standard_user", longString, "Username and password do not match",
                    "B02 - Password nhập dài trên 200 length");
            writeRow(sBoundary, 3, "nguyen' OR '1' ='1", "pass", "Username and password do not match",
                    "B03 - SQL pattern injection đơn giản vào User");
            writeRow(sBoundary, 4, "<script>alert(1)</script>", "secret_sauce", "Username and password do not match",
                    "B04 - Mã độc XSS nhúng thử qua input fields.");

            // Ghi file
            File dirs = new File("src/test/resources/testdata");
            if (!dirs.exists())
                dirs.mkdirs();

            try (FileOutputStream fos = new FileOutputStream(new File(dirs, "login_data.xlsx"))) {
                workbook.write(fos);
            }

            System.out.println("===> [SUCCESS] Đã khởi tạo xong file excel Data test. Time execution="
                    + (System.currentTimeMillis() - startTime) + "ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeHeader(Sheet sheet, String... titles) {
        Row row = sheet.createRow(0); // Dòng header là 0
        for (int i = 0; i < titles.length; i++) {
            row.createCell(i).setCellValue(titles[i]);
        }
    }

    private static void writeRow(Sheet sheet, int rowNum, String... values) {
        Row row = sheet.createRow(rowNum);
        for (int i = 0; i < values.length; i++) {
            row.createCell(i).setCellValue(values[i]);
        }
    }
}
