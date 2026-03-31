# Selenium Framework - Lab 11

Dự án này là một Selenium Framework với Page Object Model (POM), tích hợp TestNG và chạy trên GitHub Actions CI/CD.

## Cách chạy Local
1. Mở terminal tại thư mục gốc của dự án.
2. Chạy lệnh: `mvn clean test -DsuiteXmlFile=testng-smoke.xml`

## Cấu trúc
- `src/main/java/com/automation/framework/`: Common utilities (DriverFactory, BasePage, ConfigReader)
- `src/main/java/com/automation/pages/`: Page Objects
- `src/test/java/com/automation/tests/`: Test classes
- `src/test/resources/`: Configurations and test data (JSON)
- `.github/workflows/`: GitHub Actions CI pipeline
