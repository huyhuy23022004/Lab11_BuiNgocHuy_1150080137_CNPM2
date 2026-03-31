# BÀI 2: MATRIX STRATEGY – CHẠY SONG SONG ĐA BROWSER (1.0 điểm)

### 2.1 Mục tiêu
Cấu hình pipeline chạy Chrome và Firefox cùng lúc thay vì lần lượt. Kết quả: thời gian CI giảm gần một nửa.

### 2.2 Thêm Matrix Strategy vào workflow
Sửa file `selenium-ci.yml`, thêm phần `strategy` vào job `run-tests`:

```yaml
jobs:
  run-tests:
    runs-on: ubuntu-latest
    strategy:
      matrix:
        browser: [chrome, firefox] # Tạo 2 job chạy song song
      fail-fast: false           # Chrome fail không dừng Firefox

    steps:
      - name: Chạy test (${{ matrix.browser }})
        run: mvn clean test -Dbrowser=${{ matrix.browser }} -DsuiteXmlFile=testng-smoke.xml

      - name: Lưu kết quả
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: test-results-${{ matrix.browser }} # 2 artifact riêng biệt
          path: target/surefire-reports/
```

### 2.3 So sánh thời gian chạy

| Cấu hình | Thời gian | Ghi chú |
| :--- | :--- | :--- |
| Tuần tự (không matrix) | ~120 giây | Chrome xong rồi mới chạy Firefox |
| Song song (có matrix) | ~65 giây | Chrome + Firefox chạy cùng lúc |
| Tiết kiệm được | ~55 giây | Nhanh hơn khoảng 45% |

### Tiêu chí chấm điểm
- [ ] File YAML có `strategy.matrix` với `[chrome, firefox]` và `fail-fast: false`
- [ ] `DriverFactory` đã hỗ trợ Firefox headless (`-headless` argument)
- [ ] Chụp màn hình Actions tab cho thấy 2 job Chrome + Firefox chạy song song
- [ ] Ghi lại kết quả so sánh thời gian tuần tự vs song song
