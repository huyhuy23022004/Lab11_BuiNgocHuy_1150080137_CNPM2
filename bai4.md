# BÀI 4: SELENIUM GRID VỚI DOCKER (2.0 điểm)

### Selenium Grid là gì?
Grid cho phép phân phối việc chạy test ra nhiều máy (node) khác nhau.
- **Hub (Router):** Trung tâm điều phối, nhận request từ test code và phân phối đến node phù hợp.
- **Node:** Máy thực sự chạy browser, đăng ký với Hub và thực thi test.
- **Xem trạng thái tại:** `http://localhost:4444`

---

## Phần A – Khởi động Grid (0.5 điểm)

### Bước 1: Tạo file docker-compose.yml
```yaml
version: '3.8'
services:
  selenium-hub:
    image: selenium/hub:4.18.1
    container_name: selenium-hub
    ports:
      - "4442:4442"
      - "4443:4443"
      - "4444:4444" # Grid UI và WebDriver endpoint

  chrome-node-1:
    image: selenium/node-chrome:4.18.1
    shm_size: 2gb    # Quan trọng: Chrome cần shared memory
    depends_on: [ selenium-hub ]
    environment:
      - SE_EVENT_BUS_HOST=selenium-hub
      - SE_NODE_MAX_SESSIONS=3
      - SE_NODE_SESSION_TIMEOUT=300

  chrome-node-2:
    image: selenium/node-chrome:4.18.1
    shm_size: 2gb
    depends_on: [ selenium-hub ]
    environment:
      - SE_EVENT_BUS_HOST=selenium-hub
      - SE_NODE_MAX_SESSIONS=3

  firefox-node:
    image: selenium/node-firefox:4.18.1
    shm_size: 2gb
    depends_on: [ selenium-hub ]
    environment:
      - SE_EVENT_BUS_HOST=selenium-hub
      - SE_NODE_MAX_SESSIONS=2
```

### Bước 2: Khởi động và kiểm tra
```bash
docker-compose up -d     # Khởi động tất cả container ở background
docker ps                # Kiểm tra 4 container đang chạy
# Mở trình duyệt, truy cập: http://localhost:4444
# -> Chụp màn hình Grid Console UI cho thấy 3 node đã đăng ký với Hub
docker-compose down      # Tắt Grid khi xong việc
```

---

## Phần B – Kết nối Framework với Grid (0.75 điểm)

### Sửa DriverFactory.java – thêm hỗ trợ RemoteWebDriver
```java
public static WebDriver createDriver(String browser) {
    String gridUrl = System.getProperty("grid.url");
    if (gridUrl != null && !gridUrl.isBlank()) {
        return createRemoteDriver(browser, gridUrl); // Chạy trên Grid
    }
    return createLocalDriver(browser); // Chạy local bình thường
}

private static WebDriver createRemoteDriver(String browser, String gridUrl) {
    DesiredCapabilities caps = new DesiredCapabilities();
    caps.setBrowserName(browser.toLowerCase());
    if (browser.equalsIgnoreCase("chrome")) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage");
        caps.merge(options);
    }
    try {
        URL gridEndpoint = new URL(gridUrl + "/wd/hub");
        RemoteWebDriver driver = new RemoteWebDriver(gridEndpoint, caps);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        return driver;
    } catch (MalformedURLException e) {
        throw new RuntimeException("Grid URL không hợp lệ: " + gridUrl);
    }
}
```

### Tạo file testng-grid.xml – chạy song song 4 luồng
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd">
<suite name="Grid Test Suite" parallel="tests" thread-count="4">
  <test name="Chrome - LoginTest">
    <parameter name="browser" value="chrome"/>
    <classes><class name="tests.LoginTest"/></classes>
  </test>
  <test name="Chrome - CartTest">
    <parameter name="browser" value="chrome"/>
    <classes><class name="tests.CartTest"/></classes>
  </test>
  <test name="Firefox - LoginTest">
    <parameter name="browser" value="firefox"/>
    <classes><class name="tests.LoginTest"/></classes>
  </test>
  <test name="Firefox - CartTest">
    <parameter name="browser" value="firefox"/>
    <classes><class name="tests.CartTest"/></classes>
  </test>
</suite>
```

```bash
# Lệnh chạy test với Grid:
mvn test -Dgrid.url=http://localhost:4444 -DsuiteXmlFile=testng-grid.xml
# Trong lúc chạy -> mở http://localhost:4444 -> chụp màn hình nhiều session đang hoạt động
```

---

## Phần C – Đo hiệu suất (0.75 điểm)

Chuẩn bị 8 test method, đo thời gian chạy và điền vào bảng:

| Cấu hình | Số thread | Thời gian chạy | Hệ số tăng tốc |
| :--- | :--- | :--- | :--- |
| Tuần tự (local) | 1 | ... giây | 1.0x (baseline) |
| Song song Grid – 2 thread | 2 | ... giây | ... x |
| Song song Grid – 4 thread | 4 | ... giây | ... x |

### Tiêu chí chấm điểm
- [ ] `docker-compose.yml` có 1 Hub + 2 Chrome Node + 1 Firefox Node
- [ ] Chụp màn hình Grid Console UI: 3 node đã đăng ký
- [ ] `DriverFactory` hỗ trợ `RemoteWebDriver` khi có `-Dgrid.url`
- [ ] Chụp màn hình nhiều session chạy đồng thời trên Grid Console
- [ ] Điền đầy đủ bảng so sánh thời gian (3 hàng: 1 / 2 / 4 thread)
