package com.example.hsa_core.global.apiPayload.exception.handler;

import com.example.hsa_core.global.apiPayload.code.BaseCode;
import com.example.hsa_core.global.apiPayload.exception.GeneralException;

public class ErrorHandler extends GeneralException {
    public ErrorHandler(BaseCode errorCode) {
        super(errorCode);
    }
}
