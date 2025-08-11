package com.example.cloudfour.userservice.domain.auth.exception;

import com.example.cloudfour.modulecommon.apiPayLoad.code.BaseErrorCode;
import com.example.cloudfour.modulecommon.apiPayLoad.exception.CustomException;

public class AuthException extends CustomException {
    public AuthException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}


