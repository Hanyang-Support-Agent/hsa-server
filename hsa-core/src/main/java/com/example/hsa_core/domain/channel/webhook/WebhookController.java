package com.example.hsa_core.domain.channel.webhook;

import com.example.hsa_core.domain.channel.ChannelType;
import com.example.hsa_core.domain.customer.Customer;
import com.example.hsa_core.domain.customer.CustomerRepository;
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
    private final CustomerRepository customerRepository;

    @PostMapping("/channels/{channelName}")
    public ResponseEntity<Map<String, Object>> receiveInquiry(
            @PathVariable String channelName,
            @RequestBody WebhookRequestDTO request)
    {
        ChannelType channelType = ChannelType.valueOf(channelName.toUpperCase());

        Customer customer = customerRepository.findByIdentifierAndChannel_Type(request.getIdentifier(), channelType)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 외부 채널 고객입니다: " + request.getIdentifier()));

        InquiryCreateRequest inquiryRequest = new InquiryCreateRequest(
                customer.getId(),
                request.getContent(),
                channelType
        );

        InquiryCreateResponse response = inquiryService.createInquiry(inquiryRequest, channelType);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "inquiryId", response.inquiryId(),
                "message", channelName.toUpperCase() + " 채널로부터 문의가 정상 접수되었습니다."
        ));

    }
}
