package com.example.cloudfour.userservice.domain.auth.exception;

import com.example.modulecommon.apiPayLoad.code.BaseErrorCode;
import com.example.modulecommon.apiPayLoad.exception.CustomException;

public class AuthException extends CustomException {
    public AuthException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}


