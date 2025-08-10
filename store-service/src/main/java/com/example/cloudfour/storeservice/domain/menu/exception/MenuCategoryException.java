package com.example.cloudfour.storeservice.domain.menu.exception;


import com.example.modulecommon.apiPayLoad.code.BaseErrorCode;
import com.example.modulecommon.apiPayLoad.exception.CustomException;

public class MenuCategoryException extends CustomException {
    public MenuCategoryException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
