package com.example.cloudfour.cartservice.cart.service.command;

import com.example.cloudfour.cartservice.cart.converter.CartConverter;
import com.example.cloudfour.cartservice.cart.dto.CartRequestDTO;
import com.example.cloudfour.cartservice.cart.dto.CartResponseDTO;
import com.example.cloudfour.cartservice.cart.entity.Cart;
import com.example.cloudfour.cartservice.cart.exception.CartErrorCode;
import com.example.cloudfour.cartservice.cart.exception.CartException;
import com.example.cloudfour.cartservice.cart.repository.CartRepository;
import com.example.cloudfour.cartservice.cartitem.converter.CartItemConverter;
import com.example.cloudfour.cartservice.cartitem.dto.CartItemRequestDTO;
import com.example.cloudfour.cartservice.cartitem.dto.CartItemResponseDTO;
import com.example.cloudfour.cartservice.cartitem.service.command.CartItemCommandService;
import com.example.cloudfour.cartservice.commondto.MenuResponseDTO;
import com.example.cloudfour.cartservice.config.GatewayPrincipal;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CartCommandService {
    private final CartRepository cartRepository;
    private final RestTemplate restTemplate;
    private final CartItemCommandService cartItemCommandService;

    public CartResponseDTO.CartCreateResponseDTO createCart(CartRequestDTO.CartCreateRequestDTO cartCreateRequestDTO, GatewayPrincipal user) {
        UUID store =  restTemplate.getForObject("http://store-service/api/stores/{storeId}", UUID.class, cartCreateRequestDTO.getStoreId());
        UUID findUser =  restTemplate.getForObject("http://store-service/api/stores/{storeId}", UUID.class, cartCreateRequestDTO.getStoreId());
        boolean exists = cartRepository.existsByUserAndStore(findUser, store);
        if (exists) {
            throw new CartException(CartErrorCode.ALREADY_ADD);
        }
        log.info("장바구니 생성 권한 확인 완료");
        Cart cart = Cart.builder()
                .build();
        cart.setUser(findUser);
        cart.setStore(store);
        Cart savedCart = cartRepository.save(cart);
        log.info("장바구니 생성 완료, cartId={}", savedCart.getId());

        MenuResponseDTO menu = restTemplate.getForEntity("http://menu-service/api/menus/{menuId}",
                MenuResponseDTO.class, cartCreateRequestDTO.getMenuId()).getBody();
        CartItemRequestDTO.CartItemAddRequestDTO cartItemAddRequestDTO = CartItemConverter.toCartItemAddRequestDTO(cartCreateRequestDTO,menu.getPrice());
        CartItemResponseDTO.CartItemAddResponseDTO cartItemAddResponseDTO = cartItemCommandService.AddCartItem(cartItemAddRequestDTO, savedCart.getId(), user);
        return CartConverter.toCartCreateResponseDTO(savedCart,cartItemAddResponseDTO.getCartItemCommonResponseDTO().getCartItemId());

    }

    public void deleteCart(UUID cartId, GatewayPrincipal user) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartException(CartErrorCode.NOT_FOUND));
        log.info("장바구니 삭제 권한 확인 완료");
        if (!cart.getUser().equals(user)) {
            throw new CartException(CartErrorCode.UNAUTHORIZED_ACCESS);
        }
        cartRepository.delete(cart);
        log.info("장바구니 삭제 완료");
    }
}
