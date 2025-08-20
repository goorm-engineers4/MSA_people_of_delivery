package com.example.cloudfour.cartservice.domain.cart.service.command;

import com.example.cloudfour.cartservice.client.StoreClient;
import com.example.cloudfour.cartservice.domain.cart.converter.CartConverter;
import com.example.cloudfour.cartservice.domain.cart.dto.CartRequestDTO;
import com.example.cloudfour.cartservice.domain.cart.dto.CartResponseDTO;
import com.example.cloudfour.cartservice.domain.cart.entity.Cart;
import com.example.cloudfour.cartservice.domain.cart.exception.CartErrorCode;
import com.example.cloudfour.cartservice.domain.cart.exception.CartException;
import com.example.cloudfour.cartservice.domain.cart.repository.CartRepository;
import com.example.cloudfour.cartservice.domain.cartitem.converter.CartItemConverter;
import com.example.cloudfour.cartservice.domain.cartitem.dto.CartItemRequestDTO;
import com.example.cloudfour.cartservice.domain.cartitem.dto.CartItemResponseDTO;
import com.example.cloudfour.cartservice.domain.cartitem.service.command.CartItemCommandService;
import com.example.cloudfour.cartservice.commondto.MenuResponseDTO;
import com.example.cloudfour.modulecommon.dto.CurrentUser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CartCommandService {
    private final CartRepository cartRepository;
    private final CartItemCommandService cartItemCommandService;
    private final StoreClient storeClient;

    public CartResponseDTO.CartCreateResponseDTO createCart(CartRequestDTO.CartCreateRequestDTO cartCreateRequestDTO, CurrentUser user) {
        UUID store = cartCreateRequestDTO.getStoreId();
        if (!storeClient.existStore(store)) {
            throw new CartException(CartErrorCode.STORE_NOT_FOUND);
        }
        boolean exists = cartRepository.existsByUserAndStore(user.id(), store);

        if (exists) {
            log.warn("이미 존재하는 장바구니");
            throw new CartException(CartErrorCode.ALREADY_ADD);
        }
        log.info("장바구니 생성 권한 확인 완료 성공");

        Cart cart = Cart.builder()
                .build();
        cart.setUser(user.id());
        cart.setStore(store);
        Cart savedCart = cartRepository.save(cart);
        MenuResponseDTO menu = storeClient.menuById(cartCreateRequestDTO.getMenuId());
        CartItemRequestDTO.CartItemAddRequestDTO cartItemAddRequestDTO = CartItemConverter.toCartItemAddRequestDTO(cartCreateRequestDTO,menu.getPrice());
        CartItemResponseDTO.CartItemAddResponseDTO cartItemAddResponseDTO = cartItemCommandService.CreateCartItem(cartItemAddRequestDTO, savedCart.getId(), user);
        log.info("장바구니 생성 완료, cartId={}", savedCart.getId());
        return CartConverter.toCartCreateResponseDTO(savedCart,cartItemAddResponseDTO.getCartItemCommonResponseDTO().getCartItemId());
    }

    public void deleteCart(UUID cartId, CurrentUser user) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 장바구니");
                    return new CartException(CartErrorCode.NOT_FOUND);
                });
        log.info("장바구니 삭제 권한 확인 성공");
        if (!cart.getUser().equals(user.id())) {
            log.warn("장바구니 삭제 권한 없음");
            throw new CartException(CartErrorCode.UNAUTHORIZED_ACCESS);
        }
        cartRepository.delete(cart);
        log.info("장바구니 삭제 완료");
    }
}
