package com.example.cloudfour.storeservice.domain.menu.exception;

import com.example.global.apiPayLoad.code.BaseErrorCode;
import com.example.global.apiPayLoad.exception.CustomException;

public class MenuCategoryException extends CustomException {
    public MenuCategoryException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
