package com.example.hsa_core.global.apiPayload.exception;


import com.example.hsa_core.global.apiPayload.ApiResponse;
import com.example.hsa_core.global.apiPayload.code.BaseCode;
import com.example.hsa_core.global.apiPayload.code.ReasonDTO;
import com.example.hsa_core.global.apiPayload.code.status.ErrorStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 전역 예외 처리 클래스
 *
 * 애플리케이션에서 발생하는 모든 예외를 중앙에서 처리하고,
 * 공통 응답 포맷(ApiResponse)으로 클라이언트에게 전달합니다.
 *
 * 처리하는 예외 종류:
 * - ConstraintViolationException: @Valid 검증 실패
 * - MethodArgumentNotValidException: @RequestBody 검증 실패
 * - GeneralException: 비즈니스 로직에서 명시적으로 throw한 예외
 * - 기타 예외: 예상치 못한 모든 예외 (500 Internal Server Error)
 */

@RequiredArgsConstructor
@Slf4j
@RestControllerAdvice(annotations = {RestController.class})
public class ExceptionAdvice extends ResponseEntityExceptionHandler {

    // 1. ConstraintViolationException 처리
    @ExceptionHandler
    public ResponseEntity<Object> validation(ConstraintViolationException e, WebRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        e.getConstraintViolations().forEach(violation -> {
            String propertyPath = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.merge(propertyPath, message, (existing, newMsg) -> existing + ", " + newMsg);
        });

        return handleExceptionInternalConstraint(e, ErrorStatus._BAD_REQUEST, HttpHeaders.EMPTY, request, errors);
    }

    // 2. @RequestBody 검증 실패 처리 (ResponseEntityExceptionHandler의 메서드 오버라이드)
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        Map<String, String> errors = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors().forEach(fieldError -> {
            String fieldName = fieldError.getField();
            String errorMessage = Optional.ofNullable(fieldError.getDefaultMessage()).orElse("");
            errors.merge(fieldName, errorMessage, (existing, newMsg) -> existing + ", " + newMsg);
        });

        return handleExceptionInternalArgs(e, headers, ErrorStatus._BAD_REQUEST, request, errors);
    }

    // 3. 모든 예외 처리
    @ExceptionHandler
    public ResponseEntity<Object> exception(Exception e, WebRequest request) {
        log.error("Unhandled Exception 발생", e);
        return handleExceptionInternalFalse(e, ErrorStatus._INTERNAL_SERVER_ERROR, HttpHeaders.EMPTY,
                HttpStatus.INTERNAL_SERVER_ERROR, request, e.getMessage());
    }

    // 4. 비즈니스 예외(GeneralException) 처리
    @ExceptionHandler(value = GeneralException.class)
    public ResponseEntity<Object> onThrowException(GeneralException generalException, HttpServletRequest request) {
        return handleExceptionInternal(generalException, generalException.getCode(), HttpHeaders.EMPTY, request);
    }

    // --- 내부 헬퍼 메서드들 ---

    private ResponseEntity<Object> handleExceptionInternal(Exception e, BaseCode code,
                                                           HttpHeaders headers, HttpServletRequest request) {
        ApiResponse<Object> body = ApiResponse.onFailure(code, null);
        ReasonDTO reason = code.getReasonHttpStatus();
        return super.handleExceptionInternal(e, body, headers, reason.getHttpStatus(), new ServletWebRequest(request));
    }

    private ResponseEntity<Object> handleExceptionInternalFalse(Exception e, ErrorStatus errorCommonStatus,
                                                                HttpHeaders headers, HttpStatus status, WebRequest request, String errorPoint) {
        ApiResponse<Object> body = ApiResponse.onFailure(errorCommonStatus, errorPoint);
        return super.handleExceptionInternal(e, body, headers, status, request);
    }

    private ResponseEntity<Object> handleExceptionInternalArgs(Exception e, HttpHeaders headers, ErrorStatus errorCommonStatus,
                                                               WebRequest request, Map<String, String> errorArgs) {
        ApiResponse<Object> body = ApiResponse.onFailure(errorCommonStatus, errorArgs);
        return super.handleExceptionInternal(e, body, headers, errorCommonStatus.getHttpStatus(), request);
    }

    private ResponseEntity<Object> handleExceptionInternalConstraint(Exception e, ErrorStatus errorCommonStatus,
                                                                     HttpHeaders headers, WebRequest request, Map<String, String> errors) {
        ApiResponse<Object> body = ApiResponse.onFailure(errorCommonStatus, errors);
        return super.handleExceptionInternal(e, body, headers, errorCommonStatus.getHttpStatus(), request);
    }
}