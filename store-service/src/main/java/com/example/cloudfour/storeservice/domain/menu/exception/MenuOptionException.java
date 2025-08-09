package com.example.cloudfour.storeservice.domain.menu.exception;


import com.example.modulecommon.apiPayLoad.code.BaseErrorCode;
import com.example.modulecommon.apiPayLoad.exception.CustomException;

public class MenuOptionException extends CustomException {
    public MenuOptionException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
