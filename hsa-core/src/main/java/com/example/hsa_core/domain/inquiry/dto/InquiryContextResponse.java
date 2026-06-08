package com.example.hsa_core.domain.inquiry.dto;

public record InquiryContextResponse (
    String orderStatus,
    String trackingNumber,
    String carrier,
    String productName,
    String deliveryStatus,
    String currnetLocation
){
    //데이터가 비어있을 때
    public static InquiryContextResponse empty(){
        return new InquiryContextResponse("NONE", "NONE", "NONE", "NONE", "NONE", "NONE");
    }
}

