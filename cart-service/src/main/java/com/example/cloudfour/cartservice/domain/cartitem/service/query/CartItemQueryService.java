package com.example.cloudfour.cartservice.domain.cartitem.service.query;

import com.example.cloudfour.cartservice.domain.cartitem.converter.CartItemConverter;
import com.example.cloudfour.cartservice.domain.cartitem.dto.CartItemResponseDTO;
import com.example.cloudfour.cartservice.domain.cartitem.entity.CartItem;
import com.example.cloudfour.cartservice.domain.cartitem.exception.CartItemErrorCode;
import com.example.cloudfour.cartservice.domain.cartitem.exception.CartItemException;
import com.example.cloudfour.cartservice.domain.cartitem.repository.CartItemRepository;
import com.example.cloudfour.modulecommon.dto.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartItemQueryService {
    private final CartItemRepository cartItemRepository;

    public CartItemResponseDTO.CartItemListResponseDTO getCartItemById(UUID cartItemId, CurrentUser user) {
        if(user == null || !cartItemRepository.existsByCartItemAndUser(cartItemId,user.id())){
            log.warn("장바구니 아이템 조회 권한 없음");
            throw new CartItemException(CartItemErrorCode.UNAUTHORIZED_ACCESS);
        }
        log.info("장바구니 아이템 조회 권한 확인 성공");
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(()->{
                    log.warn("존재하지 않는 장바구니 아이템");
                    return new CartItemException(CartItemErrorCode.NOT_FOUND);
                });
        log.info("장바구니 아이템 조회 완료");
        return CartItemConverter.toCartItemListResponseDTO(cartItem);
    }
}
