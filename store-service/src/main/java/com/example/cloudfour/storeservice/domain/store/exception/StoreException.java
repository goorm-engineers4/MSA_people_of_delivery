package com.example.cloudfour.storeservice.domain.store.exception;

import com.example.modulecommon.apiPayLoad.code.BaseErrorCode;
import com.example.modulecommon.apiPayLoad.exception.CustomException;


public class StoreException extends CustomException {
  public StoreException(BaseErrorCode errorCode) {
    super(errorCode);
  }
}
