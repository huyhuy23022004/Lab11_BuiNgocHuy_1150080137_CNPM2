# BÀI 3: GITHUB SECRETS – BẢO MẬT CREDENTIAL (1.0 điểm)

### 3.1 Tại sao không được hardcode password?
Hardcode password vào code rồi push lên GitHub là lỗi bảo mật nghiêm trọng. Bất kỳ ai thấy repository (kể cả public) đều đọc được password của bạn.

> [!WARNING]
> **Lỗi thường gặp**
> TUYỆT ĐỐI KHÔNG làm như này:
> ```java
> String password = "secret_sauce"; // <- Lộ password lên GitHub!
> String username = "standard_user"; // <- Lộ username lên GitHub!
> ```

### 3.2 Tạo GitHub Secrets – hướng dẫn từng bước
1. Vào repository trên GitHub.
2. Click tab **Settings** (góc phải phía trên).
3. Chọn **Secrets and variables** &rarr; **Actions** ở menu bên trái.
4. Click **New repository secret**.
5. Tạo 2 secret lần lượt:
   - Name: `SAUCEDEMO_USERNAME` | Value: `standard_user`
   - Name: `SAUCEDEMO_PASSWORD` | Value: `secret_sauce`

### 3.3 Truyền Secret vào workflow YAML
```yaml
      - name: Chạy test
        run: mvn test -Dbrowser=chrome
        env:
          APP_USERNAME: ${{ secrets.SAUCEDEMO_USERNAME }}
          APP_PASSWORD: ${{ secrets.SAUCEDEMO_PASSWORD }}
        # GitHub tự động che giá trị -> log hiển thị *** thay vì giá trị thật
```

### 3.4 Đọc Secret trong Java
```java
public String getPassword() {
    // Ưu tiên đọc từ biến môi trường (khi chạy trên CI/CD)
    String password = System.getenv("APP_PASSWORD");
    if (password == null || password.isBlank()) {
        // Fallback: đọc từ file config (khi chạy local)
        password = ConfigReader.getInstance().getProperty("app.password");
    }
    return password;
}
```

### 3.5 Kiểm tra bảo mật bằng lệnh grep
Chạy 2 lệnh sau để đảm bảo không có password nào bị commit vào code:
```bash
grep -r 'secret_sauce' src/       # Kết quả phải RỖNG
grep -r 'standard_user' src/main/ # Kết quả phải RỖNG
```

> [!TIP]
> **Lưu ý:**
> Tạo thêm file `.env.example` (không commit file `.env` thật) để developer local biết cần đặt biến gì:
> ```text
> # Sao chép file này thành .env rồi điền giá trị thật vào
> APP_USERNAME=your_username_here
> APP_PASSWORD=your_password_here
> BASE_URL=https://www.saucedemo.com
> ```

### Tiêu chí chấm điểm
- [ ] Đã tạo 2 GitHub Secrets: `SAUCEDEMO_USERNAME` và `SAUCEDEMO_PASSWORD`
- [ ] Workflow YAML truyền secret qua `env` block
- [ ] Java đọc từ `System.getenv()` trước, fallback về config file
- [ ] Lệnh grep không tìm thấy password nào trong `src/`
- [ ] Pipeline chạy thành công dù credential chỉ nằm trong Secrets
