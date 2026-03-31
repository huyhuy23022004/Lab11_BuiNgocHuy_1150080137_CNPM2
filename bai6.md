# BÀI 6: PIPELINE ĐẦY ĐỦ + ALLURE LÊN GITHUB PAGES (1.5 điểm)

### 6.1 Tạo file selenium-full.yml
```yaml
# .github/workflows/selenium-full.yml
name: Full Selenium CI Pipeline

on:
  push:
    branches: [ main ]
  schedule:
    - cron: '0 2 * * 1-5' # 2:00 AM thứ 2 đến thứ 6

jobs:
  test:
    runs-on: ubuntu-latest
    strategy:
      matrix:
        browser: [chrome, firefox]
      fail-fast: false
    
    steps:
      - uses: actions/checkout@v4
      
      - uses: actions/setup-java@v4
        with: { java-version: '17', distribution: 'temurin', cache: maven }
        
      - name: Chạy test
        run: mvn clean test -Dbrowser=${{ matrix.browser }} -DsuiteXmlFile=testng-smoke.xml
        env:
          APP_USERNAME: ${{ secrets.SAUCEDEMO_USERNAME }}
          APP_PASSWORD: ${{ secrets.SAUCEDEMO_PASSWORD }}
          
      - name: Lưu kết quả cho Allure
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: allure-results-${{ matrix.browser }}
          path: target/allure-results/

  publish-report:
    needs: test       # Chạy sau khi tất cả job test xong
    if: always()      # Kể cả khi test fail
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      
      - name: Download kết quả Chrome
        uses: actions/download-artifact@v4
        with: { name: allure-results-chrome, path: allure-results/ }
        
      - name: Download kết quả Firefox
        uses: actions/download-artifact@v4
        with: { name: allure-results-firefox, path: allure-results/ }
        
      - name: Tạo và Publish Allure Report
        uses: simple-elf/allure-report-action@master
        with:
          allure_results: allure-results
          gh_pages: gh-pages
          allure_report: allure-report
```

### 6.2 Bật GitHub Pages – hướng dẫn từng bước
1. Vào **Settings** của repository.
2. Chọn **Pages** ở menu bên trái.
3. Source &rarr; **Deploy from a branch**.
4. Branch &rarr; **gh-pages** &rarr; **Save**.

> [!NOTE]
> **Lưu ý**
> Sau khi pipeline chạy xong, Allure Report sẽ xuất hiện tại:
> `https://{username}.github.io/{tên-repo}/`
> 
> *Ví dụ:* `https://nguyenvana.github.io/selenium-framework/`

### 6.3 Thêm Badge vào README.md
```markdown
[![Test Status](https://github.com/{user}/{repo}/actions/workflows/selenium-full.yml/badge.svg)](https://github.com/{user}/{repo}/actions)
[![Allure Report](https://img.shields.io/badge/Allure-Report-orange)](https://{user}.github.io/{repo}/)
```

### Tiêu chí chấm điểm
- [ ] File `selenium-full.yml` có cả 2 job: `test` (matrix) + `publish-report`
- [ ] GitHub Pages đã bật, Allure Report xuất hiện tại link GitHub Pages
- [ ] Chụp màn hình link GitHub Pages đang hoạt động với Allure Report
- [ ] `README.md` có 2 badge: Test Status + Allure Report
