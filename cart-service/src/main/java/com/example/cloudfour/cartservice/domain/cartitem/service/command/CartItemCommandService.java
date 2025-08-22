package com.example.cloudfour.cartservice.domain.cartitem.service.command;

import com.example.cloudfour.cartservice.client.StoreClient;
import com.example.cloudfour.cartservice.domain.cart.entity.Cart;
import com.example.cloudfour.cartservice.domain.cart.exception.CartErrorCode;
import com.example.cloudfour.cartservice.domain.cart.exception.CartException;
import com.example.cloudfour.cartservice.domain.cart.repository.CartRepository;
import com.example.cloudfour.cartservice.domain.cartitem.converter.CartItemConverter;
import com.example.cloudfour.cartservice.domain.cartitem.dto.CartItemRequestDTO;
import com.example.cloudfour.cartservice.domain.cartitem.dto.CartItemResponseDTO;
import com.example.cloudfour.cartservice.domain.cartitem.entity.CartItem;
import com.example.cloudfour.cartservice.domain.cartitem.exception.CartItemErrorCode;
import com.example.cloudfour.cartservice.domain.cartitem.exception.CartItemException;
import com.example.cloudfour.cartservice.domain.cartitem.repository.CartItemRepository;
import com.example.cloudfour.cartservice.commondto.MenuOptionResponseDTO;
import com.example.cloudfour.cartservice.commondto.MenuResponseDTO;
import com.example.cloudfour.modulecommon.dto.CurrentUser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.example.cloudfour.cartservice.domain.cartitem.entity.CartItemOption;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CartItemCommandService {
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final StoreClient storeClient;

    public CartItemResponseDTO.CartItemAddResponseDTO CreateCartItem(CartItemRequestDTO.CartItemAddRequestDTO cartItemAddRequestDTO, UUID cartId, CurrentUser user) {
        Cart cart = cartRepository.findByIdAndUser(cartId, user.id())
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 장바구니");
                    return new CartException(CartErrorCode.NOT_FOUND);
                });

        if(user==null){
            log.warn("장바구니 아이템 추가 권한 없음");
            throw new CartItemException(CartItemErrorCode.UNAUTHORIZED_ACCESS);
        }
        UUID menu = cartItemAddRequestDTO.getMenuId();

        if (!storeClient.existMenu(menu)) {
            throw new CartException(CartErrorCode.MENU_NOT_FOUND);
        }

        log.info("장바구니 아이템 추가 권한 확인 성공");

        List<UUID> selectedOptionIds =
                Optional.ofNullable(cartItemAddRequestDTO.getMenuOptionIds()).orElseGet(List::of);
        
        int additionalPrice = 0;
        if (!selectedOptionIds.isEmpty()) {
            List<MenuOptionResponseDTO> options = storeClient.menuOptionsByIds(selectedOptionIds);
            additionalPrice = options.stream()
                    .mapToInt(MenuOptionResponseDTO::getAdditionalPrice)
                    .sum();
        }

        CartItem cartItem = CartItem.builder()
                .quantity(1)
                .price(0 + additionalPrice)
                .build();

        cartItem.setCart(cart);
        cartItem.setMenu(menu);

        if (!selectedOptionIds.isEmpty()) {
            List<MenuOptionResponseDTO> options = storeClient.menuOptionsByIds(selectedOptionIds);
            for (MenuOptionResponseDTO optionResponse : options) {
                CartItemOption option = CartItemOption.builder()
                        .menuOptionId(optionResponse.getMenuOptionId())
                        .additionalPrice(optionResponse.getAdditionalPrice())
                        .optionName(optionResponse.getOptionName())
                        .build();
                cartItem.addOption(option);
            }
        }

        cartItemRepository.save(cartItem);

        log.info("장바구니 아이템 추가 완료");
        return CartItemConverter.toCartItemAddResponseDTO(cartItem);
    }

    public CartItemResponseDTO.CartItemAddResponseDTO AddCartItem(CartItemRequestDTO.CartItemAddRequestDTO cartItemAddRequestDTO, UUID cartId, CurrentUser user) {
        Cart cart = cartRepository.findByIdAndUser(cartId, user.id())
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 장바구니");
                    return new CartException(CartErrorCode.NOT_FOUND);
                });

        if(user==null){
            log.warn("장바구니 아이템 생성 권한 없음");
            throw new CartItemException(CartItemErrorCode.UNAUTHORIZED_ACCESS);
        }
        log.info("장바구니 아이템 생성 권한 확인 성공");

        MenuResponseDTO menu = storeClient.menuById(cartItemAddRequestDTO.getMenuId());
        log.info("메뉴 데이터 가져옴: {}", menu.getMenuId());

        List<UUID> selectedOptionIds =
                Optional.ofNullable(cartItemAddRequestDTO.getMenuOptionIds()).orElseGet(List::of);

        List<MenuOptionResponseDTO> options = selectedOptionIds.isEmpty()
                ? List.of()
                : storeClient.menuOptionsByIds(selectedOptionIds);

        boolean invalid = options.stream().anyMatch(opt -> !opt.getMenuId().equals(menu.getMenuId()));
        if (invalid) {
            log.warn("요청한 옵션 중 메뉴와 소속이 다른 옵션 존재");
            throw new CartItemException(CartItemErrorCode.INVALID_OPTION);
        }

        int basePrice = menu.getPrice();
        int additional = options.stream().mapToInt(MenuOptionResponseDTO::getAdditionalPrice).sum();
        int unitPrice = basePrice + additional;

        int quantity = 1;
        if (quantity <= 0) {
            log.warn("수량이 0 이하: {}", quantity);
            throw new CartItemException(CartItemErrorCode.INVALID_QUANTITY);
        }

        int totalPrice = unitPrice * quantity;

        CartItem cartItem = CartItem.builder()
                .quantity(quantity)
                .price(totalPrice)
                .build();

        cartItem.setCart(cart);
        cartItem.setMenu(menu.getMenuId());

        for (MenuOptionResponseDTO optionResponse : options) {
            CartItemOption option = CartItemOption.builder()
                    .menuOptionId(optionResponse.getMenuOptionId())
                    .additionalPrice(optionResponse.getAdditionalPrice())
                    .optionName(optionResponse.getOptionName())
                    .build();
            cartItem.addOption(option);
        }

        cartItemRepository.save(cartItem);
        log.info("장바구니 아이템 생성 완료 (cartId={}, itemId={})", cart.getId(), cartItem.getId());

        return CartItemConverter.toCartItemAddResponseDTO(cartItem);
    }

    public CartItemResponseDTO.CartItemUpdateResponseDTO updateCartItem(CartItemRequestDTO.CartItemUpdateRequestDTO cartItemUpdateRequestDTO, UUID cartItemId, CurrentUser user) {
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(()->{
            log.warn("존재하지 않는 장바구니 아이템");
            return new CartItemException(CartItemErrorCode.NOT_FOUND);
        });
        if(user == null || !cartItemRepository.existsByCartItemAndUser(cartItemId,user.id())){
            log.warn("장바구니 아이템 수정 권한 없음");
            throw new CartItemException(CartItemErrorCode.UNAUTHORIZED_ACCESS);
        }

        log.info("장바구니 아이템 수정 권한 확인 성공");

        List<UUID> selectedOptionIds =
                Optional.ofNullable(cartItemUpdateRequestDTO.getMenuOptionIds()).orElseGet(List::of);
        
        int additionalPrice = 0;
        if (!selectedOptionIds.isEmpty()) {
            List<MenuOptionResponseDTO> options = storeClient.menuOptionsByIds(selectedOptionIds);
            additionalPrice = options.stream()
                    .mapToInt(MenuOptionResponseDTO::getAdditionalPrice)
                    .sum();
        }

        MenuResponseDTO menu = storeClient.menuById(cartItem.getMenu());

        Integer quantity = cartItemUpdateRequestDTO.getQuantity();
        if(quantity <=0){
            log.info("수량이 0보다 적으므로 장바구니 수정 취소");
            throw new CartItemException(CartItemErrorCode.UPDATE_FAILED);
        }

        int totalPrice = (menu.getPrice() + additionalPrice) * quantity;
        cartItem.update(quantity, totalPrice);

        cartItem.getOptions().clear();
        if (!selectedOptionIds.isEmpty()) {
            List<MenuOptionResponseDTO> options = storeClient.menuOptionsByIds(selectedOptionIds);
            for (MenuOptionResponseDTO optionResponse : options) {
                CartItemOption option = CartItemOption.builder()
                        .menuOptionId(optionResponse.getMenuOptionId())
                        .additionalPrice(optionResponse.getAdditionalPrice())
                        .optionName(optionResponse.getOptionName())
                        .build();
                cartItem.addOption(option);
            }
        }
        
        cartItemRepository.save(cartItem);
        log.info("장바구니 아이템 수정 완료");
        return CartItemConverter.toCartItemUpdateResponseDTO(cartItem);
    }

    public void deleteCartItem(UUID cartItemId, CurrentUser user) {
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(()->{
            log.warn("존재하지 않는 장바구니 아이템");
            return new CartItemException(CartItemErrorCode.NOT_FOUND);
        });
        Cart cart = cartRepository.findById(cartItem.getCart().getId()).orElseThrow(()->{
            log.warn("존재하지 않는 장바구니");
            return new CartException(CartErrorCode.NOT_FOUND);
        });
        if(user == null || !cartItemRepository.existsByCartItemAndUser(cartItemId,user.id())){
            log.warn("장바구니 아이템 삭제 권한 없음");
            throw new CartItemException(CartItemErrorCode.UNAUTHORIZED_ACCESS);
        }
        log.info("장바구니 아이템 삭제 권한 확인 성공");
        cartItemRepository.delete(cartItem);
        cartItemRepository.flush();
        if(cart.getCartItems().isEmpty()){
            cartRepository.delete(cart);
        }
        log.info("장바구니 아이템 삭제 완료");
    }
}
