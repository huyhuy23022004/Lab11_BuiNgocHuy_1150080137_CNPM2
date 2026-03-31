package com.lab9.bai6;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Trình phân tích Retry của TestNG.
 * Sẽ xem Test vừa kết thúc có thuộc diện bị Fail hay không, và nếu Fail thì đã
 * vượt quá Hạn Mức Tối Đa Retry chưa.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private int count = 0;

    // Đọc số lượng retry cao nhất dc cấu hình lấy từ ConfigReader
    private static final int maxTry = ConfigReader.getMaxRetryCount();

    @Override
    public boolean retry(ITestResult result) {
        if (!result.isSuccess()) { // Nếu Test thất bại
            if (count < maxTry) { // Và chưa chạm ngưỡng
                count++;
                // Báo cáo lại log console trạng thái test đang được TestNG retry
                String message = String.format("[Retry] Đang retry test '%s' lần %d/", result.getName(), count)
                        + maxTry;
                System.out.println(message);

                result.setStatus(ITestResult.FAILURE); // Cố định Status cũ
                return true; // Yêu cầu TestNG Chạy lại Test đó
            } else {
                result.setStatus(ITestResult.FAILURE); // Đã vượt qua maxTry, công nhận là Thất bại vĩnh viễn
            }
        } else {
            result.setStatus(ITestResult.SUCCESS); // Nếu test Pass thì bỏ qua
        }
        return false;
    }
}
