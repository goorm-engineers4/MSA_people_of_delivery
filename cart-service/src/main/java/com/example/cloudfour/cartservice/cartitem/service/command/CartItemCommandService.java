package com.example.cloudfour.cartservice.cartitem.service.command;

import com.example.cloudfour.cartservice.cart.entity.Cart;
import com.example.cloudfour.cartservice.cart.exception.CartErrorCode;
import com.example.cloudfour.cartservice.cart.exception.CartException;
import com.example.cloudfour.cartservice.cart.repository.CartRepository;
import com.example.cloudfour.cartservice.cartitem.converter.CartItemConverter;
import com.example.cloudfour.cartservice.cartitem.dto.CartItemRequestDTO;
import com.example.cloudfour.cartservice.cartitem.dto.CartItemResponseDTO;
import com.example.cloudfour.cartservice.cartitem.entity.CartItem;
import com.example.cloudfour.cartservice.cartitem.exception.CartItemErrorCode;
import com.example.cloudfour.cartservice.cartitem.exception.CartItemException;
import com.example.cloudfour.cartservice.cartitem.repository.CartItemRepository;
import com.example.cloudfour.cartservice.commondto.MenuOptionResponseDTO;
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
public class CartItemCommandService {
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final RestTemplate restTemplate;

    public CartItemResponseDTO.CartItemAddResponseDTO AddCartItem(CartItemRequestDTO.CartItemAddRequestDTO cartItemAddRequestDTO, UUID cartId, GatewayPrincipal user) {
        Cart cart = cartRepository.findByIdAndUser(cartId, user.userId())
                .orElseThrow(() -> new CartItemException(CartItemErrorCode.NOT_FOUND));

        UUID menu = restTemplate.getForObject("http://menu-service/api/menus/{menuId}/detail", UUID.class, cartItemAddRequestDTO.getMenuId());
        MenuOptionResponseDTO menuOption = restTemplate.getForObject("http://menu-service/api/menus/options/{optionId}/detail",
                MenuOptionResponseDTO.class, cartItemAddRequestDTO.getMenuOptionId());

        log.info("장바구니 아이템 추가 권한 확인");

        CartItem cartItem = CartItem.builder()
                .quantity(cartItemAddRequestDTO.getQuantity())
                .price(cartItemAddRequestDTO.getPrice()+menuOption.getAdditionalPrice())
                .build();

        cartItem.setCart(cart);
        cartItem.setMenu(menu);
        cartItem.setMenuOption(menuOption.getMenuOptionId());

        cartItemRepository.save(cartItem);

        log.info("장바구니 아이템 추가 완료");
        return CartItemConverter.toCartItemAddResponseDTO(cartItem);
    }

    public CartItemResponseDTO.CartItemAddResponseDTO CreateCartItem(CartItemRequestDTO.CartItemCreateRequestDTO cartItemCreateRequestDTO, UUID cartId, GatewayPrincipal user) {
        Cart cart = cartRepository.findByIdAndUser(cartId, user.userId())
                .orElseThrow(() -> new CartItemException(CartItemErrorCode.NOT_FOUND));

        log.info("장바구니 아이템 생성 권한 확인");
        MenuResponseDTO menu = restTemplate.getForObject("http://menu-service/api/menus/{menuId}/detail",
                MenuResponseDTO.class, cartItemCreateRequestDTO.getMenuId());
        MenuOptionResponseDTO menuOption = restTemplate.getForObject("http://menu-service/api/menus/options/{optionId}/detail",
                MenuOptionResponseDTO.class, cartItemCreateRequestDTO.getMenuOptionId());

        CartItem cartItem = CartItem.builder()
                .quantity(1)
                .price(menu.getPrice()+menuOption.getAdditionalPrice())
                .build();

        cartItem.setCart(cart);
        cartItem.setMenu(menu.getMenuId());
        cartItem.setMenuOption(menuOption.getMenuOptionId());

        cartItemRepository.save(cartItem);

        log.info("장바구니 아이템 생성 완료");
        return CartItemConverter.toCartItemAddResponseDTO(cartItem);
    }

    public CartItemResponseDTO.CartItemUpdateResponseDTO updateCartItem(CartItemRequestDTO.CartItemUpdateRequestDTO cartItemUpdateRequestDTO, UUID cartItemId, GatewayPrincipal user) {
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(()->new CartItemException(CartItemErrorCode.NOT_FOUND));
        if(!cartItemRepository.existsByCartItemAndUser(cartItemId,user.userId())){
            throw new CartItemException(CartItemErrorCode.UNAUTHORIZED_ACCESS);
        }

        log.info("장바구니 아이템 수정 권한 확인");
        MenuOptionResponseDTO menuOption = restTemplate.getForObject("http://menu-service/api/menus/options/{optionId}/detail",
                MenuOptionResponseDTO.class, cartItemUpdateRequestDTO.getMenuOptionId());

        MenuResponseDTO menu = restTemplate.getForObject("http://menu-service/api/menus/{menuId}/detail",
                MenuResponseDTO.class,  menuOption.getMenuId());

        Integer quantity = cartItemUpdateRequestDTO.getQuantity();
        if(quantity <=0){
            log.info("수량이 0보다 적으므로 장바구니 수정 취소");
            throw new CartItemException(CartItemErrorCode.UPDATE_FAILED);
        }

        cartItem.update(quantity, quantity*(menu.getPrice()+menuOption.getAdditionalPrice()));
        cartItem.setMenuOption(menuOption.getMenuOptionId());
        cartItemRepository.save(cartItem);
        log.info("장바구니 아이템 수정 완료");
        return CartItemConverter.toCartItemUpdateResponseDTO(cartItem);
    }

    public void deleteCartItem(UUID cartItemId, GatewayPrincipal user) {
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(()->new CartItemException(CartItemErrorCode.NOT_FOUND));
        Cart cart = cartRepository.findById(cartItem.getCart().getId()).orElseThrow(()->new CartException(CartErrorCode.NOT_FOUND));
        if(!cartItemRepository.existsByCartItemAndUser(cartItemId,user.userId())){
            throw new CartItemException(CartItemErrorCode.UNAUTHORIZED_ACCESS);
        }
        log.info("장바구니 아이템 삭제 권한 확인");
        cartItemRepository.delete(cartItem);
        cartItemRepository.flush();
        if(cart.getCartItems().isEmpty()){
            cartRepository.delete(cart);
        }
        log.info("장바구니 아이템 삭제 완료");
    }
}
