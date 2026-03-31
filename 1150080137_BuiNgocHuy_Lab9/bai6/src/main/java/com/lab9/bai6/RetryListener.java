package com.lab9.bai6;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Đắng kí Listener dùng AnnotationTransformer giúp áp dụng 'RetryAnalyzer' cho
 * tất cả các @Test nằm trong dự án một cách tự động toàn cầu thay vì phải gõ
 * thủ công cho từng test hàm.
 */
public class RetryListener implements IAnnotationTransformer {

    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        // Gán tự động RetryAnalyzer
        annotation.setRetryAnalyzer(RetryAnalyzer.class);
    }
}
