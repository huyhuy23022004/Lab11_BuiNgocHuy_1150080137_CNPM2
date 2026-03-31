package com.lab9.bai4;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

public class UserLoginTest extends BaseTest {

    @DataProvider(name = "jsonLoginProvider")
    public Object[][] getLoginData() {
        List<UserData> users = JsonReader.readUsers("testdata/users.json");
        Object[][] data = new Object[users.size()][1];

        for (int i = 0; i < users.size(); i++) {
            // Nhồi trực tiếp nguyên Class UserData cho DataProvider
            data[i][0] = users.get(i);
        }
        return data;
    }

    @Test(dataProvider = "jsonLoginProvider")
    public void testLoginWithJsonData(UserData user) {
        System.out.println("===> THỰC THI TEST: " + user.getDescription());

        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.open();
        loginPage.login(user.getUsername(), user.getPassword());

        if (user.isExpectSuccess()) {
            // Case success sẽ chuyển hướng sang Inventory. Ngược lại url vẫn đứng yên.
            String currentUrl = getDriver().getCurrentUrl();
            Assert.assertTrue(currentUrl.contains("inventory"),
                    "Login expected SUCCESS nhưng lại fail. URL=" + currentUrl);
        } else {
            // Case failed phải hiện lỗi
            Assert.assertTrue(loginPage.isErrorDisplayed(),
                    "Login expected FAIL nhưng không xuất hiện Error message cảnh báo.");
        }
    }
}
