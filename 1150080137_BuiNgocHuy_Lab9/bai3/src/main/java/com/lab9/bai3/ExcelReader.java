package com.lab9.bai3;

import org.apache.poi.ss.usermodel.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * ExcelReader - Tiện ích đọc file Excel.
 * Cung cấp khả năng định dạng chính xác các field NUMERIC, STRING, BOOLEAN,
 * FORMULA, NULL.
 */
public class ExcelReader {

    /**
     * Lấy toàn bộ dữ liệu từ một Sheet (bỏ qua dòng Header đầu tiên).
     *
     * @param fileName  tên file (nằm trong thư mục resources, vd:
     *                  "testdata/login_data.xlsx")
     * @param sheetName tên sheet ("SmokeCases", "NegativeCases", "BoundaryCases")
     * @return một list Object[] tương ứng cho `@DataProvider`
     */
    public static List<Object[]> readSheetData(String fileName, String sheetName) {
        List<Object[]> dataList = new ArrayList<>();

        try (InputStream is = ExcelReader.class.getClassLoader().getResourceAsStream(fileName)) {
            if (is == null) {
                throw new RuntimeException("Không tìm thấy file " + fileName + " trong classpath.");
            }

            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheet(sheetName);

            if (sheet == null) {
                throw new RuntimeException("Không tìm thấy sheet: " + sheetName);
            }

            // Data bắt đầu từ dòng 2 (index = 1), bỏ qua header dòng 1 (index = 0)
            int rowCount = sheet.getLastRowNum();
            int colCount = sheet.getRow(0).getLastCellNum();

            for (int i = 1; i <= rowCount; i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue; // Bỏ qua dòng trống

                // Đọc Object[] gồm { sheetName, val1, val2, val3, val4 }
                // Có chứa sheetName làm index 0 để dễ Assert logic theo loại sheet
                Object[] rowData = new Object[colCount + 1];
                rowData[0] = sheetName;

                for (int j = 0; j < colCount; j++) {
                    Cell cell = row.getCell(j);
                    // Dịch index cho data để nhường mảng 0 cho sheetName
                    rowData[j + 1] = getCellValueAsString(cell);
                }

                dataList.add(rowData);
            }
            workbook.close();

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi đọc file Excel " + fileName + ": " + e.getMessage(), e);
        }

        return dataList;
    }

    /**
     * Phương thức xử lý cell null và 4 kiểu dữ liệu: STRING, NUMERIC, BOOLEAN,
     * FORMULA.
     * Quy định chuyển đổi trả về toàn bộ dữ liệu ở định dạng String an toàn.
     * 
     * @param cell Apache POI Cell
     * @return Dữ liệu String an toàn hoặc String rỗng nếu null
     */
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return ""; // Xử lý null
        }

        CellType cellType = cell.getCellType();

        // Xử lý ô dạng FORMULA (tính toán kết quả trả về type gì)
        if (cellType == CellType.FORMULA) {
            cellType = cell.getCachedFormulaResultType();
        }

        switch (cellType) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                // Tránh trường hợp đọc số int lại thành double có .0 (như "123.0")
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == Math.floor(numericValue)) {
                        return String.valueOf((long) numericValue); // convert 123.0 to 123
                    }
                    return String.valueOf(numericValue);
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case BLANK:
            case ERROR:
            case _NONE:
            default:
                return "";
        }
    }
}
