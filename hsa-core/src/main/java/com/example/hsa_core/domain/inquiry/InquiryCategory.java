package com.example.hsa_core.domain.inquiry;

// AI가 문의 내용을 분류할 때 사용하는 문의 유형입니다.
public enum InquiryCategory {
    DELIVERY, //배송 문의
    EXCHANGE_REFUND, //교환,환불 문의
    PRODUCT, //상품 문의
    OTHER //기타
}
