# Selenium Framework - Lab 11

Dự án Automation Test với Selenium Webdriver, TestNG và tích hợp CI/CD qua GitHub Actions (Bài 1 - Lab 11).

## Yêu cầu môi trường
- Java 17
- Maven

## Cài đặt thư viện
Dự án sử dụng Maven. Tải các dependencies bằng lệnh:
```bash
mvn clean install -DskipTests
```

## Cách chạy test local
```bash
mvn clean test -Dbrowser=chrome -Denv=dev -DsuiteXmlFile=testng-smoke.xml
```

## Tích hợp CI/CD (GitHub Actions)
Repository này đã thiết lập CI/CD tại đường dẫn `.github/workflows/selenium-ci.yml`.
Mỗi khi có code mới được `push` lên `main` hoặc `develop`, hệ thống tự động:
1. Clone source code
2. Thiết lập Java 17 (cache maven)
3. Chạy test trong chế độ Headless
4. Lưu screenshot/reports dưới dạng artifact.
