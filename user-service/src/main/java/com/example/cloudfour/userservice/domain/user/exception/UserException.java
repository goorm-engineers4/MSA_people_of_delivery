package com.example.cloudfour.userservice.domain.user.exception;

import com.example.modulecommon.apiPayLoad.code.BaseErrorCode;
import com.example.modulecommon.apiPayLoad.exception.CustomException;

public class UserException extends CustomException {
    public UserException(BaseErrorCode errorCode) {
      super(errorCode);
    }
}
