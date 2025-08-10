package com.example.cloudfour.storeservice.domain.store.exception;

import com.example.modulecommon.apiPayLoad.code.BaseErrorCode;
import com.example.modulecommon.apiPayLoad.exception.CustomException;

public class StoreCategoryException extends CustomException {
    public StoreCategoryException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
