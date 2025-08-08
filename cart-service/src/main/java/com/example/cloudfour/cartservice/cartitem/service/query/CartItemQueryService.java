package com.example.cloudfour.cartservice.cartitem.service.query;

import com.example.cloudfour.cartservice.cart.repository.CartRepository;
import com.example.cloudfour.cartservice.cartitem.converter.CartItemConverter;
import com.example.cloudfour.cartservice.cartitem.dto.CartItemResponseDTO;
import com.example.cloudfour.cartservice.cartitem.entity.CartItem;
import com.example.cloudfour.cartservice.cartitem.exception.CartItemErrorCode;
import com.example.cloudfour.cartservice.cartitem.exception.CartItemException;
import com.example.cloudfour.cartservice.cartitem.repository.CartItemRepository;
import com.example.cloudfour.cartservice.config.GatewayPrincipal;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CartItemQueryService {
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;

    public CartItemResponseDTO.CartItemListResponseDTO getCartItemById(UUID cartItemId, GatewayPrincipal user) {
        if(!cartItemRepository.existsByCartItemAndUser(cartItemId,user.userId())){
            throw new CartItemException(CartItemErrorCode.UNAUTHORIZED_ACCESS);
        }
        log.info("장바구니 아이템 조회 권한 확인");
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(()->new CartItemException(CartItemErrorCode.NOT_FOUND));
        log.info("장바구니 아이템 조회 완료");
        return CartItemConverter.toCartItemListResponseDTO(cartItem);
    }

    public List<CartItemResponseDTO.CartItemListResponseDTO>getCartItemList(UUID cartId, GatewayPrincipal user) {
        if(!cartRepository.existsByUserAndCart(user.userId(),cartId)){
            throw new CartItemException(CartItemErrorCode.UNAUTHORIZED_ACCESS);
        }
        log.info("장바구니 아이템 목록 조회 권한 확인");
        List<CartItem> cartItem = cartItemRepository.findAllByCartId(cartId,user.userId());
        if(cartItem.isEmpty()){
            throw new CartItemException(CartItemErrorCode.NOT_FOUND);
        }
        log.info("장바구니 아이템 목록 조회 완료");
        return cartItem.stream().map(CartItemConverter::toCartItemListResponseDTO).toList();
    }
}
