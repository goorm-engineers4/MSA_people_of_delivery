package com.example.cloudfour.cartservice.cart.exception;

import com.example.cloudfour.modulecommon.apiPayLoad.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum CartErrorCode implements BaseErrorCode {
    CREATE_FAILED(HttpStatus.BAD_REQUEST, "CART400_1", "장바구니 정보를 생성할 수 없습니다."),
    DELETE_FAILED(HttpStatus.BAD_REQUEST, "CART400_2", "장바구니 정보를 삭제할 수 없습니다."),
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "CART401", "장바구니에 접근할 수 있는 권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "CART404", "장바구니를 찾을 수 없습니다."),
    ALREADY_ADD(HttpStatus.CONFLICT, "CART409", "이미 등록된 장바구니입니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "CART500", "장바구니 처리 중 서버 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
