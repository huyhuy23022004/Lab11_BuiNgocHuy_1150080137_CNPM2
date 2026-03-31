# BÀI 1: GITHUB ACTIONS CI/CD CƠ BẢN (1.5 điểm)

### 1.1 Mục tiêu
Tạo repository GitHub và thiết lập pipeline CI/CD đầu tiên: mỗi khi push code lên GitHub, Selenium test sẽ tự động chạy mà không cần bạn làm gì thêm.

### 1.2 Bước chuẩn bị
1. Push framework từ Lab 9 lên GitHub repository mới đặt tên là `selenium-framework`.
2. Thêm file `README.md` mô tả ngắn về project và cách chạy local.
3. Tạo file `.gitignore` để tránh commit file rác lên GitHub.

> [!IMPORTANT]
> **Lưu ý:** Nội dung `.gitignore` tối thiểu cần có:
> ```text
> target/
> .idea/
> *.iml
> screenshots/
> .env
> *.log
> ```

### 1.3 Tạo file GitHub Actions Workflow
Tạo thư mục `.github/workflows/` trong project, rồi tạo file `selenium-ci.yml` bên trong:

```yaml
# .github/workflows/selenium-ci.yml
name: Selenium Test Suite

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]
  workflow_dispatch: # Cho phép bấm nút chạy thủ công

jobs:
  run-tests:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Cài Java 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven # Cache để lần sau build nhanh hơn

      - name: Chạy Selenium Tests
        run: mvn clean test -Dbrowser=chrome -Denv=dev -DsuiteXmlFile=testng-smoke.xml
        env:
           APP_PASSWORD: ${{ secrets.SAUCEDEMO_PASSWORD }}

      - name: Lưu kết quả test
        if: always() # Chạy dù test pass hay fail
        uses: actions/upload-artifact@v4
        with:
          name: test-results
          path: |
            target/surefire-reports/
            target/screenshots/
          retention-days: 30
```

### 1.4 Bật Headless mode trong DriverFactory.java
CI server không có màn hình thật &rarr; phải bật Headless, nếu không sẽ báo lỗi "cannot open display".

```java
public class DriverFactory {
    public static WebDriver createDriver(String browser) {
        // GitHub Actions tự đặt biến CI=true
        boolean isCI = System.getenv("CI") != null;
        return switch (browser.toLowerCase()) {
            case "firefox" -> createFirefoxDriver(isCI);
            default -> createChromeDriver(isCI);
        };
    }

    private static WebDriver createChromeDriver(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless=new"); // Chrome 112+
            options.addArguments("--no-sandbox");   // Bắt buộc trên Linux CI
            options.addArguments("--disable-dev-shm-usage"); // Tránh lỗi OOM
            options.addArguments("--window-size=1920,1080");
        } else {
            options.addArguments("--start-maximized");
        }
        WebDriverManager.chromedriver().setup();
        return new ChromeDriver(options);
    }

    private static WebDriver createFirefoxDriver(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless) options.addArguments("-headless");
        WebDriverManager.firefoxdriver().setup();
        return new FirefoxDriver(options);
    }
}
```

### Lỗi thường gặp
*   **Lỗi 1:** `'Chrome not found'` &rarr; Ubuntu Latest đã có Chrome sẵn, dùng WebDriverManager để match version.
*   **Lỗi 2:** `'cannot open display :0'` &rarr; Bạn quên bật `--headless`, xem lại `DriverFactory`.
*   **Lỗi 3:** `'Out of memory'` &rarr; Thêm `--disable-dev-shm-usage` vào `ChromeOptions`.
*   **Lỗi 4:** `'Element not interactable'` &rarr; CI chậm hơn local, hãy tăng explicit wait.
*   **Lỗi 5:** `'Test pass local nhưng fail CI'` &rarr; Thường do timing, kiểm tra lại implicit/explicit wait.

### Tiêu chí chấm điểm
- [ ] File `.github/workflows/selenium-ci.yml` tồn tại trong repo.
- [ ] Pipeline chạy thành công &rarr; chụp màn hình log màu xanh.
- [ ] Tạo 1 test sai assertion cố ý &rarr; push &rarr; chụp màn hình log màu đỏ.
- [ ] Download artifact &rarr; xem ảnh chụp màn hình của test bị fail.
