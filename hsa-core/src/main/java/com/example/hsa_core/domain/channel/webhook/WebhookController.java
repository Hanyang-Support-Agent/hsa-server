package com.example.hsa_core.domain.channel.webhook;

import com.example.hsa_core.domain.inquiry.dto.InquiryCreateRequest;
import com.example.hsa_core.domain.inquiry.dto.InquiryCreateResponse;
import com.example.hsa_core.domain.inquiry.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
public class WebhookController {
    private final InquiryService inquiryService;

    @PostMapping("/channels/{channelName}")
    public ResponseEntity<Map<String, Object>> receiveInquiry(
            @PathVariable String channelName,
            @RequestBody WebhookRequestDTO request)
    {
        InquiryCreateRequest inquiryRequest = new InquiryCreateRequest(
                request.getCustomerId(),
                request.getContent()
        );

        InquiryCreateResponse response = inquiryService.createInquiry(inquiryRequest);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "inquiryId", response.inquiryId(),
                "message", channelName.toUpperCase() + " 채널로부터 문의가 정상 접수되었습니다."
        ));

    }
}
