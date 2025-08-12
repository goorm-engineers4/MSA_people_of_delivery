package com.example.cloudfour.aiservice.exception;

public class AiLogException extends RuntimeException {
    private final AiLogErrorCode errorCode;

    public AiLogException(AiLogErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public AiLogErrorCode getErrorCode() {
        return errorCode;
    }
}
