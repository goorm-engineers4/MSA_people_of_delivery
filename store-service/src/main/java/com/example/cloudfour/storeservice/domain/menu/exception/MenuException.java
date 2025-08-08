package com.example.cloudfour.storeservice.domain.menu.exception;

import com.example.global.apiPayLoad.code.BaseErrorCode;
import com.example.global.apiPayLoad.exception.CustomException;

public class MenuException extends CustomException {
    public MenuException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
