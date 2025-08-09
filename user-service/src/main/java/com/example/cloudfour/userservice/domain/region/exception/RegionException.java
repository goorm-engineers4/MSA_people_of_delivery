package com.example.cloudfour.userservice.domain.region.exception;

import com.example.modulecommon.apiPayLoad.code.BaseErrorCode;
import com.example.modulecommon.apiPayLoad.exception.CustomException;

public class RegionException extends CustomException {
    public RegionException(BaseErrorCode errorCode) {
      super(errorCode);
    }
}
