# ShopEasy Project – Test Strategy & Test Plan (Sprint 5)

**Vị trí:** QA Lead  
**Dự án:** ShopEasy (E-commerce Application)  
**Ngày soạn thảo:** 31/03/2026

---

## PHẦN A: TEST STRATEGY (CHIẾN LƯỢC KIỂM THỬ)

### 1. Phạm vi kiểm thử (Scope)
Trong giai đoạn này, ShopEasy tập trung vào việc ra mắt chức năng thanh toán trả góp. Tuy nhiên, để đảm bảo tính ổn định của hệ thống lõi, phạm vi kiểm thử bao gồm:

| Loại | Module / Tính năng | Lý do |
| :--- | :--- | :--- |
| **IN SCOPE** | Đăng ký & Đăng nhập | Đảm bảo khách hàng có thể vào hệ thống an toàn để mua hàng. |
| **IN SCOPE** | Tìm kiếm & Giỏ hàng | Luồng chính để dẫn tới bước thanh toán. |
| **IN SCOPE** | Thanh toán trả góp VPBank | **Tính năng trọng tâm của Sprint 5.** Đòi hỏi kiểm tra kỹ về API tích hợp. |
| **IN SCOPE** | Quản lý đơn hàng | Khách hàng cần xem lại lịch sử trả góp và trạng thái đơn hàng. |
| **OUT SCOPE** | Hệ thống Admin backend | Dành cho nội bộ, sẽ được kiểm thử ở giai đoạn sau (Phase 2). |
| **OUT SCOPE** | Phân tích báo cáo (BI) | Tính năng phụ trợ, không ảnh hưởng đến trải nghiệm thanh toán của người dùng. |

### 2. Phân loại kiểm thử và Tỉ lệ áp dụng
Dựa trên kim tự tháp kiểm thử (Testing Pyramid) và đặc thù của dự án ShopEasy:

*   **Unit Test (20%) - Công cụ: JUnit 5, Mockito:**
    *   *Lý do:* Kiểm tra các logic tính toán tiền lãi, số kỳ trả góp ngay tại tầng code. Developer chịu trách nhiệm đảm bảo các hàm logic chạy đúng.
*   **API Test (45%) - Công cụ: RestAssured:**
    *   *Lý do:* Vì ShopEasy sử dụng kiến trúc Microservices, API đóng vai trò huyết mạch. Việc kiểm thử tích hợp API với sandbox của VPBank là tối quan trọng để đảm bảo dữ liệu truyền đi chính xác và an toàn.
*   **UI Test (20%) - Công cụ: Selenium & Page Object Model:**
    *   *Lý do:* Kiểm tra trải nghiệm người dùng cuối trên Web. Đảm bảo giao diện hiển thị các nút chọn kỳ hạn trả góp trực quan và dễ sử dụng.
*   **Performance Test (10%) - Công cụ: JMeter:**
    *   *Lý do:* ShopEasy thường có các chiến dịch "Flash Sale". Hệ thống cần đảm bảo chịu tải được ít nhất 10,000 CCU trong lúc thanh toán trả góp mà không bị treo.
*   **Security Test (5%) - Công cụ: OWASP ZAP:**
    *   *Lý do:* Thanh toán liên quan đến thông tin nhạy cảm. Cần rà quét lỗ hổng SQL Injection và Cross-site Scripting (XSS) để bảo vệ dữ liệu người dùng.

### 3. Definition of Done (DoD) – Tiêu chí hoàn thành
Một tính năng được coi là "Ready for Production" khi thỏa mãn:
*   **Smoke Test:** 100% các kịch bản quan trọng phải Pass.
*   **Regression Test:** Tỷ lệ thành công đạt ít nhất 95%.
*   **Bugs:** Không còn lỗi P1 (Blocker) và P2 (Critical) nào tồn tại. Lỗi P3 (Major) phải có kế hoạch fix rõ ràng.
*   **Coverage:** Độ bao phủ code (Code Coverage) đạt trên 80%.
*   **Approval:** Allure Report được duyệt bởi QA Lead và PM.

### 4. Quản lý rủi ro (Risk Management)
| Rủi ro | Xác suất | Tác động | Kế hoạch giảm thiểu |
| :--- | :--- | :--- | :--- |
| Sandbox VPBank mất kết nối | Cao | Block toàn bộ test thanh toán | Xây dựng Mock Server nội bộ giả lập phản hồi của ngân hàng. |
| Staging environment không ổn định | Trung bình | Gây ra kết quả False Negative | Thiết lập Pipeline tự động khôi phục môi trường và dọn dẹp DB mỗi tối. |
| API thay đổi mà không báo trước | Thấp | Code test bị lỗi hàng loạt | Áp dụng Contract Testing để phát hiện sớm sự thay đổi cấu trúc API. |
| Rò rỉ thông tin thẻ trên log | Thấp | Vi phạm bảo mật PCI-DSS | Quy định không log các trường nhạy cảm, sử dụng thư viện log masking. |

### 5. Lịch trình kiểm thử (Lịch trình Sprint 14 ngày)
*   **Ngày 1-2:** Phân tích Requirements, viết Test Cases.
*   **Ngày 3-5:** Thực hiện API Testing trên Integration branch.
*   **Ngày 6-10:** Chạy UI Automation và Regression Testing.
*   **Ngày 11-12:** Thực hiện Performance & Security Scan.
*   **Ngày 13-14:** Báo cáo kết quả, Fix bug và chuẩn bị Release.

---

## PHẦN B: TEST PLAN SPRINT 5 (TÍNH NĂNG TRẢ GÓP VPBANK)

### 1. Phân tích rủi ro nghiệp vụ (Payment Risks)
5 kịch bản có thể dẫn đến thất thoát tài chính hoặc khiếu nại của khách hàng:
1.  **Tính sai lãi suất:** Đơn hàng >= 3tr nhưng khách vẫn bị tính phí 0.5% lãi/tháng do logic code sai.
2.  **Trừ tiền 2 lần:** Xảy ra race condition khi người dùng click liên tiếp vào nút "Thanh toán".
3.  **Hủy đơn vẫn bị trừ tiền:** Khách nhấn hủy ở bước cuối nhưng hệ thống không gửi callback kịp thời tới ngân hàng để hủy giao dịch.
4.  **Lỗ hổng làm tròn (Rounding issue):** Tổng số tiền trả 12 tháng không khớp với giá trị gốc của đơn hàng, dẫn đến chênh lệch tiền trong sổ cái.
5.  **Timeout nhưng vẫn trừ tiền:** API ngân hàng phản hồi chậm, ShopEasy báo "Lỗi hệ thống" nhưng thực tế tài khoản khách đã bị trừ tiền.

### 2. Danh sách 15 Test Cases
| TC-ID | Tiêu đề | Loại | Ưu tiên | Kết quả mong đợi |
| :--- | :--- | :--- | :--- | :--- |
| TC-P-01 | Trả góp 3 tháng, đơn hàng 3.5tr | API | P1 | Status=201, Interest=0 |
| TC-P-02 | Trả góp 6 tháng, đơn hàng 10tr | UI | P1 | Hiển thị đúng biểu phí kỳ hạn 6 tháng |
| TC-P-03 | Trả góp đơn hàng 2.9tr (Dưới ngưỡng) | API | P1 | Status=400, "Order value below threshold" |
| TC-P-04 | Nhập thẻ VPBank không hợp lệ | E2E | P1 | Thông báo lỗi "Card number invalid" |
| TC-P-05 | Click Login nhiều lần khi thanh toán | UI | P2 | Hệ thống chỉ xử lý 1 request đầu tiên |
| TC-P-06 | Thanh toán khi số dư thẻ không đủ | API | P1 | Status=402, "Insufficient balance" |
| TC-P-07 | Hiển thị tóm tắt trả góp (Gốc + Lãi + Phí) | UI | P2 | Các chỉ số hiển thị rõ ràng trước khi bấm Confirm |
| TC-P-08 | Email xác nhận gửi ngay sau khi trả góp thành công | API | P2 | Email gửi đúng format và có thông tin trả góp |
| TC-P-09 | Test Load: 50 user thanh toán cùng lúc | Perf | P1 | Không có giao dịch nào bị treo hoặc fail |
| TC-P-10 | Nhấn nút "Back" trình duyệt khi đang thanh toán | UI | P3 | Không tạo giao dịch rác, giữ nguyên trạng thái đơn |
| TC-P-11 | Trả góp với thẻ của ngân hàng khác (Không phải VPBank) | API | P2 | Hệ thống tự động từ chối hoặc chuyển qua visa thường |
| TC-P-12 | Tính số tiền mỗi kỳ cho đơn 3,000,001đ | Unit | P1 | Chia đều 3 tháng không lỗi làm tròn lẻ |
| TC-P-13 | Đơn hàng bị hết hàng ngay trước khi thanh toán | E2E | P2 | Giao dịch ngân hàng không thực hiện, báo "Out of stock" |
| TC-P-14 | VPBank API Gateway phản hồi lỗi 503 | API | P2 | ShopEasy hiển thị "Bank system maintenance" |
| TC-P-15 | Kiểm tra tính bảo mật của POST Payload thanh toán | Security | P1 | Token thanh toán không bị lộ trong log hoặc URL |

### 3. Xác định Blockers (Rào cản)
*   **Blocker 1:** Tài liệu đặc tả API VPBank cập nhật trễ -> *Giải pháp:* Dùng Mock API dựa trên bản draft.
*   **Blocker 2:** Môi trường Staging bị chiếm dụng bởi team khác -> *Giải pháp:* Sử dụng Docker để tự build môi trường test cô lập.
*   **Blocker 3:** Thiếu tài khoản VPBank Sandbox để test E2E -> *Giải pháp:* Đề xuất BA mua hoặc mượn tài khoản test ngay từ tuần 1 của Sprint.
