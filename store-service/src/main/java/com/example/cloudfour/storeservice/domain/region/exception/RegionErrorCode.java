package com.example.cloudfour.storeservice.domain.region.exception;

import com.example.global.apiPayLoad.code.BaseErrorCode;
import com.example.global.apiPayLoad.exception.CustomException; //추후 아마도 common에 포함될 class ...?

public class RegionException extends CustomException {
    public RegionException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
