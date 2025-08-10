package com.example.cloudfour.cartservice.order.exception;


import com.example.cloudfour.modulecommon.apiPayLoad.code.BaseErrorCode;
import com.example.cloudfour.modulecommon.apiPayLoad.exception.CustomException;

public class OrderItemException extends CustomException {
    public OrderItemException(BaseErrorCode errorCode) {
      super(errorCode);
    }
}
