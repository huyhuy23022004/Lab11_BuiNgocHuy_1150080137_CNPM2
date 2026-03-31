# BÀI 7: SOẠN TEST STRATEGY VÀ TEST PLAN THỰC TẾ (2.0 điểm)

> [!NOTE]
> **Bối cảnh dự án ShopEasy – bạn đóng vai QA Lead**
> - **Dự án:** Ứng dụng mua sắm online – ShopEasy
> - **Tech stack:** Java Spring Boot (API) + React (Web) + React Native (Mobile)
> - **Team:** 4 Developer (2 backend, 2 frontend), 1 QA (bạn), 1 Designer, 1 PM
> - **Sprint:** 2 tuần | **Release:** Mỗi 4 tuần lên production
> - **Môi trường:** Dev (localhost) &rarr; Staging (staging.shopeasy.vn) &rarr; Production (shopeasy.vn)
> - **Sprint 5 goal:** Ra mắt tính năng "Thanh toán trả góp qua VPBank"

---

## PHẦN A – Test Strategy Document (1.0 điểm)
*(Tối thiểu 600 từ, trình bày theo 5 phần dưới đây:)*

### 1. Phạm vi kiểm thử (Scope)
| Loại | Module / Tính năng | Lý do |
| :--- | :--- | :--- |
| IN SCOPE | Đăng ký tài khoản | Core feature, ảnh hưởng người dùng mới |
| IN SCOPE | Đăng nhập / Xác thực | Bảo mật, rủi ro cao |
| IN SCOPE | Tìm kiếm sản phẩm | Tính năng chính, dùng hàng ngày |
| IN SCOPE | Giỏ hàng | Tiến hành mua sắm, ảnh hưởng doanh thu |
| IN SCOPE | Thanh toán | Liên quan tiền thật, rủi ro cao nhất |
| OUT SCOPE | Admin Dashboard | Phase 2, chưa có trong Sprint này |
| OUT SCOPE | Báo cáo thống kê | Phase 2, không ảnh hưởng end-user |

### 2. Phân loại test và tỉ lệ
| Loại test | Tỉ lệ | Công cụ | Lý do chọn |
| :--- | :--- | :--- | :--- |
| Unit Test | 20% | JUnit 5 + Mockito | Developer tự test, nhanh, rẻ |
| API Test | 45% | RestAssured | Thương mại điện tử phụ thuộc API rất nhiều |
| UI Test (Selenium) | 20% | Selenium + POM | Kiểm tra trải nghiệm người dùng |
| Performance Test | 10% | JMeter | Hệ thống phải chịu tải cao ngày sale |
| Security Test | 5% | OWASP ZAP | Có dữ liệu thẻ tín dụng, bắt buộc phải test |

### 3. Definition of Done – Khi nào thì "đã test xong"?
- Smoke test: 100% PASS
- Regression test: &ge; 95% PASS
- Không có bug P1 (Blocker) nào đang mở
- Không có bug P2 (Critical) nào chưa có kế hoạch xử lý
- Code coverage &ge; 80% (đo bằng JaCoCo)
- Allure Report đã được team xem xét và xác nhận

### 4. Quản lý rủi ro
| Rủi ro | Xác suất | Tác động | Kế hoạch giảm thiểu |
| :--- | :--- | :--- | :--- |
| Sandbox VPBank không ổn định | Cao | Không test được thanh toán | Liên hệ VPBank sớm, có mock API dự phòng |
| Staging data bị xóa đột xuất | Trung bình | Phải tạo lại test data | Script tự động tạo test data trước mỗi lần test |
| API 3rd party bị down | Trung bình | Test E2E bị block | Mock service cho test offline |
| CI server hết disk space | Thấp | Pipeline không chạy được | Tự động xóa artifact cũ hàng tuần |

### 5. Lịch trình kiểm thử
| Loại test | Khi nào chạy | Thời gian | Trigger |
| :--- | :--- | :--- | :--- |
| Smoke Test | Sau mỗi commit | ~5 phút | Tự động – GitHub Actions |
| Regression Test | Hàng đêm 2:00 AM | ~45 phút | Cron schedule |
| Performance Test | Mỗi tuần (Chủ nhật) | ~2 giờ | Thủ công hoặc cron |
| Security Scan | Trước mỗi release | ~3 giờ | Thủ công trước release |
| UAT | Cuối mỗi Sprint | 2–3 ngày | Thủ công, có PO tham gia |

---

## PHẦN B – Test Plan cho Sprint 5 (1.0 điểm)
**Tính năng:** Thanh toán trả góp VPBank (3/6/12 tháng, phí 0% với đơn hàng &ge; 3 triệu)

### Phân tích rủi ro nghiệp vụ – 5 kịch bản có thể gây mất tiền người dùng
1. Đơn < 3 triệu nhưng hệ thống vẫn cho phép trả góp &rarr; người dùng bị thu phí ngoài ý muốn.
2. Tính số tiền trả mỗi tháng sai &rarr; người dùng bị trừ tiền không đúng.
3. Thanh toán thành công nhưng đơn hàng không được xác nhận &rarr; mất tiền, không có hàng.
4. Hệ thống lỗi giữa chừng, tiền đã bị trừ nhưng chưa confirm &rarr; tiền mất, trạng thái không rõ.
5. Session hết hạn giữa quá trình thanh toán &rarr; người dùng bị đăng xuất, mất thông tin đơn hàng. *(Lưu ý: Đánh số đã được điều chỉnh lại từ 1-5 thay vì từ 14-18 như trong ảnh tài liệu)*

### 15 Test Case cho Sprint 5
| TC-ID | Tiêu đề | Loại | Ưu tiên | Kết quả mong đợi |
| :--- | :--- | :--- | :--- | :--- |
| TC-001 | Thanh toán trả góp 3 tháng, đơn &ge; 3tr | API | P1 | 201, status=APPROVED |
| TC-002 | Bị từ chối khi đơn < 3 triệu | API | P1 | 400, error=ORDER_TOO_SMALL |
| TC-003 | UI hiển thị đúng 3 tùy chọn: 3/6/12 tháng | UI | P1 | 3 radio button hiển thị đúng |
| TC-004 | Tính số tiền trả/tháng chính xác | Unit | P1 | Số tiền = gốc/kỳ, phí = 0 |
| TC-005 | Hiển thị tổng tiền trả góp rõ ràng | UI | P2 | Hiển thị đủ: tiền gốc + phí |
| TC-006 | Thanh toán 6 tháng thành công | API | P1 | 201, status=APPROVED |
| TC-007 | Thanh toán 12 tháng thành công | API | P1 | 201, status=APPROVED |
| TC-008 | Trả góp với tài khoản VPBank hợp lệ | E2E | P1 | Đơn hàng được APPROVED |
| TC-009 | Trả góp với thẻ VPBank hết hạn | API | P1 | 400, error=CARD_EXPIRED |
| TC-010 | Trả góp khi VPBank sandbox lỗi | API | P2 | 503, thông báo lỗi thân thiện |
| TC-011 | Nhấn 'Hủy' giữa chừng không trừ tiền | E2E | P1 | Tiền không bị trừ |
| TC-012 | Session hết hạn giữa thanh toán | E2E | P2 | Chuyển về đăng nhập, đơn lưu lại |
| TC-013 | Không hiện trả góp với đơn < 3tr | UI | P2 | Tùy chọn trả góp bị ẩn |
| TC-014 | Email xác nhận gửi sau khi trả góp thành công | API | P2 | Email gửi trong 5 phút |
| TC-015 | Lịch sử đơn hàng hiển thị trả góp chính xác | UI | P3 | Chi tiết kỳ, số tiền hiển thị đúng |

### Xác định Blockers – điều gì có thể ngăn kiểm thử đúng hạn?

> [!WARNING]
> **Lỗi thường gặp**
> 1. Môi trường sandbox VPBank chưa ready (xác suất cao)
>    &rarr; **Giải pháp:** Yêu cầu VPBank cung cấp sandbox trước Sprint 2 tuần
> 2. Test data tài khoản ngân hàng chưa có
>    &rarr; **Giải pháp:** Làm việc với BA/PM để có tài khoản test VPBank ngay đầu Sprint
> 3. API thanh toán chưa có tài liệu (Swagger/Postman collection)
>    &rarr; **Giải pháp:** Yêu cầu Backend viết API contract trước khi dev xong
> 4. Môi trường staging chưa deploy code Sprint 5
>    &rarr; **Giải pháp:** Chia làm 2 giai đoạn: test với mock (tuần 1) + test staging (tuần 2)

---

### Pipeline riêng cho Sprint 5 – nhánh feature/vpbank-payment
```yaml
# Thêm vào on: section trong selenium-full.yml
on:
  push:
    branches: [ main, feature/vpbank-payment ]
  pull_request:
    branches: [ main, feature/vpbank-payment ]

# Thêm job mới chỉ chạy khi PR nhắm vào nhánh payment
  payment-test:
    if: github.base_ref == 'feature/vpbank-payment'
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { java-version: '17', distribution: 'temurin', cache: maven }
      - name: Chạy Payment Tests
        run: mvn test -DsuiteXmlFile=testng-payment.xml
        env:
          VPBANK_API_KEY: ${{ secrets.VPBANK_API_KEY }}
          APP_USERNAME: ${{ secrets.SAUCEDEMO_USERNAME }}
          APP_PASSWORD: ${{ secrets.SAUCEDEMO_PASSWORD }}
```

### Tiêu chí chấm điểm
- [ ] Test Strategy &ge; 600 từ, có đủ 5 phần: Scope, Phân loại, DoD, Rủi ro, Lịch trình
- [ ] Phân loại test có tỉ lệ % và giải thích lý do cụ thể
- [ ] Liệt kê &ge; 4 rủi ro với xác suất, tác động, kế hoạch giảm thiểu
- [ ] 5 kịch bản rủi ro nghiệp vụ liên quan đến tiền trong Sprint 5
- [ ] 15 Test Case có đầy đủ: TC-ID, Tiêu đề, Loại, Ưu tiên, Kết quả mong đợi
- [ ] Xác định Blockers có kế hoạch xử lý cụ thể
- [ ] Thêm pipeline riêng cho nhánh `feature/vpbank-payment`
