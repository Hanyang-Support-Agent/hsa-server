package com.example.hsa_core.domain.response.controller;

import com.example.hsa_core.domain.response.Response;
import com.example.hsa_core.domain.response.Transmission;
import com.example.hsa_core.domain.response.dto.ResponseConfirmRequest;
import com.example.hsa_core.domain.response.dto.ResponseModifyRequest;
import com.example.hsa_core.domain.response.dto.ResponseResponse;
import com.example.hsa_core.domain.response.dto.ResponseSendRequest;
import com.example.hsa_core.domain.response.dto.TransmissionResponse;
import com.example.hsa_core.domain.response.service.ResponseService;
import com.example.hsa_core.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Response", description = "관리자 응답 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class ResponseController {

    private final ResponseService responseService;

    @Operation(summary = "문의별 응답 목록 조회")
    @GetMapping("/inquiries/{inquiryId}/responses")
    // 문의에 연결된 전체 응답 목록을 반환합니다.
    public ResponseEntity<ApiResponse<List<ResponseResponse>>> getResponsesByInquiryId(
            @PathVariable Long inquiryId
    ) {
        List<ResponseResponse> responses = responseService.getResponsesByInquiryId(inquiryId).stream()
                .map(ResponseResponse::from)
                .toList();

        return ResponseEntity.ok(ApiResponse.onSuccess(responses));
    }

    @Operation(summary = "문의의 최신 응답 조회")
    @GetMapping("/inquiries/{inquiryId}/responses/latest")
    // 문의에 가장 최근에 생성된 응답을 반환합니다.
    public ResponseEntity<ApiResponse<ResponseResponse>> getLatestResponseByInquiryId(
            @PathVariable Long inquiryId
    ) {
        Response response = responseService.getLatestResponseByInquiryId(inquiryId);

        return ResponseEntity.ok(ApiResponse.onSuccess(ResponseResponse.from(response)));
    }

    @Operation(summary = "응답 발송 내역 조회")
    @GetMapping("/responses/{responseId}/transmissions")
    // 특정 응답의 발송 시도 및 결과 내역을 반환합니다.
    public ResponseEntity<ApiResponse<List<TransmissionResponse>>> getTransmissionsByResponseId(
            @PathVariable Long responseId
    ) {
        List<TransmissionResponse> transmissions = responseService.getTransmissionsByResponseId(responseId).stream()
                .map(TransmissionResponse::from)
                .toList();

        return ResponseEntity.ok(ApiResponse.onSuccess(transmissions));
    }

    @Operation(summary = "답변 수정")
    @PatchMapping("/responses/{responseId}")
    // 관리자가 수정한 최종 답변 내용을 반영합니다.
    public ResponseEntity<ApiResponse<ResponseResponse>> modifyResponse(
            @PathVariable Long responseId,
            @Valid @RequestBody ResponseModifyRequest request
    ) {
        Response response = responseService.modifyResponse(
                responseId,
                request.adminId(),
                request.finalContent()
        );

        return ResponseEntity.ok(ApiResponse.onSuccess(ResponseResponse.from(response)));
    }

    @Operation(summary = "답변 최종 확정")
    @PatchMapping("/responses/{responseId}/confirm")
    // 최종 답변을 발송 가능한 상태로 확정합니다.
    public ResponseEntity<ApiResponse<ResponseResponse>> confirmResponse(
            @PathVariable Long responseId,
            @Valid @RequestBody ResponseConfirmRequest request
    ) {
        Response response = responseService.confirmResponse(responseId, request.adminId());

        return ResponseEntity.ok(ApiResponse.onSuccess(ResponseResponse.from(response)));
    }

    @Operation(summary = "Mock 응답 발송")
    @PostMapping("/responses/{responseId}/send")
    // 실제 외부 연동 없이 Mock 방식으로 응답 발송을 처리합니다.
    public ResponseEntity<ApiResponse<TransmissionResponse>> sendResponseMock(
            @PathVariable Long responseId,
            @Valid @RequestBody ResponseSendRequest request
    ) {
        Transmission transmission = responseService.sendResponseMock(
                responseId,
                request.channelId(),
                request.recipientIdentifier()
        );

        return ResponseEntity.ok(ApiResponse.onSuccess(TransmissionResponse.from(transmission)));
    }
}
