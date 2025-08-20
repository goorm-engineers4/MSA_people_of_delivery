package com.example.cloudfour.cartservice.domain.cart.service.query;

import com.example.cloudfour.cartservice.domain.cart.converter.CartConverter;
import com.example.cloudfour.cartservice.domain.cart.dto.CartResponseDTO;
import com.example.cloudfour.cartservice.domain.cart.entity.Cart;
import com.example.cloudfour.cartservice.domain.cart.exception.CartErrorCode;
import com.example.cloudfour.cartservice.domain.cart.exception.CartException;
import com.example.cloudfour.cartservice.domain.cart.repository.CartRepository;
import com.example.cloudfour.modulecommon.dto.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartQueryService {
    private final CartRepository cartRepository;

    public CartResponseDTO.CartDetailResponseDTO getCartListById(UUID cartId, CurrentUser user) {
        Cart cart = cartRepository.findByIdAndUser(cartId, user.id())
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 장바구니");
                    return new CartException(CartErrorCode.NOT_FOUND);
                });
        log.info("장바구니 목록 조회 권한 확인 성공");
        return CartConverter.toCartDetailResponseDTO(cart);
    }
}
