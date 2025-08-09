package com.example.cloudfour.userservice.domain.user.exception;

import com.example.modulecommon.apiPayLoad.code.BaseErrorCode;
import com.example.modulecommon.apiPayLoad.exception.CustomException;

public class UserAddressException extends CustomException {
    public UserAddressException(BaseErrorCode errorCode) {
      super(errorCode);
    }
}
