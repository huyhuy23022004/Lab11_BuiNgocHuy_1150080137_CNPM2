package com.lab9.bai3;

import org.testng.Assert;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * LoginExcelTest - Kiểm thử Data-Driven với file Excel.
 * - Triển khai ITest để TestNG HTML Report hiển thị Description đúng thay thế
 * test method mặc định.
 * - Group "smoke" chỉ đọc file SmokeCases.
 * - Group "regression" đọc tất cả 3 sheets (SmokeCases, NegativeCases,
 * BoundaryCases).
 */
public class LoginExcelTest extends BaseTest implements ITest {

    private final ThreadLocal<String> testNameLocal = new ThreadLocal<>();

    @Override
    public String getTestName() {
        return testNameLocal.get() != null ? testNameLocal.get() : "LoginExcelTest";
    }

    /**
     * @BeforeMethod gán description từ item[4] vào Tên Test.
     */
    @BeforeMethod(alwaysRun = true)
    public void setTestName(Method method, Object[] testData) {
        super.setUp(); // Chạy BaseTest Setup trước

        if (testData != null && testData.length >= 5) {
            String sheet = testData[0].toString();
            String desc = testData[4].toString();
            testNameLocal.set("[" + sheet + "] " + desc);
        } else {
            testNameLocal.set(method.getName());
        }
    }

    /**
     * Dữ liệu điều khiển tự động bởi thuộc tính của group
     */
    @DataProvider(name = "excelLoginData")
    public Object[][] getLoginData(ITestContext context) {
        String dataFile = "testdata/login_data.xlsx";
        List<Object[]> globalData = new ArrayList<>();

        // Kiểm tra xem suite xml gọi chạy groups nào
        List<String> includedGroups = Arrays.asList(context.getIncludedGroups());
        boolean isSmoke = includedGroups.contains("smoke");
        boolean isRegression = includedGroups.contains("regression") || includedGroups.isEmpty();

        if (isSmoke || isRegression) {
            // Smoke sẽ chỉ đọc sheet "SmokeCases" (>= 3 dòng)
            globalData.addAll(ExcelReader.readSheetData(dataFile, "SmokeCases"));
        }

        if (isRegression) {
            // Regression đọc luôn 2 sheet còn lại (>= 5 và >= 4 dòng)
            globalData.addAll(ExcelReader.readSheetData(dataFile, "NegativeCases"));
            globalData.addAll(ExcelReader.readSheetData(dataFile, "BoundaryCases"));
        }

        return globalData.toArray(new Object[0][0]);
    }

    /**
     * Test chung 1 method. Sẽ tự động phân tích verify hành động từ Sheet nào:
     * - SmokeCases: verify bằng URL sau khi login (do thành công).
     * - NegativeCases / BoundaryCases: verify bằng GetErrorMessage.
     */
    @Test(dataProvider = "excelLoginData", groups = { "smoke", "regression" })
    public void testDynamicLogin(String sheetName, String usr, String pwd, String expected, String description) {
        // Log báo cáo khi đang chạy để biết method đang chạy Description nào
        System.out.println(">>> RUNNING: " + getTestName());

        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.open();

        loginPage.login(usr, pwd);

        if (sheetName.equals("SmokeCases")) {
            // Test success path
            String currentUrl = getDriver().getCurrentUrl();
            Assert.assertTrue(currentUrl.contains(expected),
                    "Expect URL chứa '" + expected + "'. Nhưng lấy dc URL thật: " + currentUrl);
        } else {
            // Test failed path
            Assert.assertTrue(loginPage.isErrorDisplayed(), "Dự kiến hiển thị Lỗi, không xuất hiện message");
            String actualError = loginPage.getErrorMessage();
            Assert.assertTrue(actualError.contains(expected),
                    "Sai thông báo lỗi. Actual: " + actualError + " | expected chứa: " + expected);
        }
    }
}
