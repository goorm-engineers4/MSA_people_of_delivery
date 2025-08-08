package com.example.cloudfour.cartservice.cart.service.query;

import com.example.cloudfour.cartservice.cart.converter.CartConverter;
import com.example.cloudfour.cartservice.cart.dto.CartResponseDTO;
import com.example.cloudfour.cartservice.cart.entity.Cart;
import com.example.cloudfour.cartservice.cart.exception.CartErrorCode;
import com.example.cloudfour.cartservice.cart.exception.CartException;
import com.example.cloudfour.cartservice.cart.repository.CartRepository;
import com.example.cloudfour.cartservice.config.GatewayPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartQueryService {
    private final CartRepository cartRepository;

    public CartResponseDTO.CartDetailResponseDTO getCartListById(UUID cartId, GatewayPrincipal user) {
        Cart cart = cartRepository.findByIdAndUser(cartId, user.userId())
                .orElseThrow(() -> new CartException(CartErrorCode.NOT_FOUND));
        log.info("장바구니 목록 조회 권한 확인 완료");
        return CartConverter.toCartDetailResponseDTO(cart);
    }
}
