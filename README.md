# Software Testing Lab 11 - Selenium CI/CD Framework

Dự án này là bộ khung kiểm thử tự động sử dụng Selenium, TestNG và Maven, được tích hợp đầy đủ quy trình CI/CD.

## 📊 Trạng thái dự án và Báo cáo

| Loại | Trạng thái |
| :--- | :--- |
| **Test Execution** | [![Full Selenium CI Pipeline - Bai 6](https://github.com/huyhuy23022004/Lab11_BuiNgocHuy_1150080137_CNPM2/actions/workflows/selenium-full.yml/badge.svg)](https://github.com/huyhuy23022004/Lab11_BuiNgocHuy_1150080137_CNPM2/actions) |
| **Allure Report** | [![Allure Report](https://img.shields.io/badge/Allure-Report-orange)](https://huyhuy23022004.github.io/Lab11_BuiNgocHuy_1150080137_CNPM2/) |

---

## 📂 Cấu trúc các bài tập

- **Bài 1:** Cấu hình CI cơ bản với GitHub Actions.
- **Bài 2:** Chạy song song đa trình duyệt (Matrix Strategy).
- **Bài 3:** Bảo mật tài khoản với GitHub Secrets.
- **Bài 4:** Selenium Grid với Docker Compose.
- **Bài 5:** Báo cáo Allure chuyên sâu với Annotation & Screenshot.
- **Bài 6:** Pipeline hoàn chỉnh tích hợp GitHub Pages.

---

## 🛠️ Cách chạy local (Bài 6)

1. **Yêu cầu:** Cài đặt Java 17, Maven và Docker.
2. **Setup Grid:** 
   ```bash
   cd bai6
   docker-compose up -d
   ```
3. **Chạy Test:**
   ```bash
   mvn clean test "-DsuiteXmlFile=testng-smoke.xml"
   ```
4. **Xem Report:**
   ```bash
   mvn allure:report
   ```
