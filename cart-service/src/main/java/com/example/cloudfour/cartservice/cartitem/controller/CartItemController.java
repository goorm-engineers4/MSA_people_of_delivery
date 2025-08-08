package com.example.cloudfour.cartservice.cartitem.controller;

import com.example.cloudfour.cartservice.cartitem.dto.CartItemRequestDTO;
import com.example.cloudfour.cartservice.cartitem.dto.CartItemResponseDTO;
import com.example.cloudfour.cartservice.cartitem.service.command.CartItemCommandService;
import com.example.cloudfour.cartservice.cartitem.service.query.CartItemQueryService;
import com.example.cloudfour.cartservice.config.GatewayPrincipal;
import com.example.cloudfour.modulecommon.apiPayLoad.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cartItems")
@RequiredArgsConstructor
@Tag(name = "CartItem", description = "장바구니아이템 API by 조성칠")
public class CartItemController {
    private final CartItemCommandService cartItemCommandService;
    private final CartItemQueryService cartItemIdQueryService;

    @PostMapping("/{cartId}")
    @Operation(summary = "장바구니 항목 추가", description = "장바구니 항목을 추가합니다. 장바구니 항목 추가에 사용되는 API입니다.")
    public CustomResponse<CartItemResponseDTO.CartItemAddResponseDTO> addCartItem(
            @PathVariable("cartId") UUID cartId,
            @RequestBody CartItemRequestDTO.CartItemCreateRequestDTO cartItemCreateRequestDTO,
            @AuthenticationPrincipal GatewayPrincipal user
    ){
        CartItemResponseDTO.CartItemAddResponseDTO cartItem = cartItemCommandService.CreateCartItem(cartItemCreateRequestDTO, cartId, user);
        return CustomResponse.onSuccess(HttpStatus.CREATED, cartItem);
    }

    @GetMapping("/{cartItemId}")
    @Operation(summary = "장바구니 항목 조회", description = "장바구니 항목을 조회합니다. 장바구니 항목 조회에 사용되는 API입니다.")
    public CustomResponse<CartItemResponseDTO.CartItemListResponseDTO> getCartItem(
            @PathVariable("cartItemId") UUID cartItemId,
            @AuthenticationPrincipal GatewayPrincipal user
    ){
        CartItemResponseDTO.CartItemListResponseDTO cartItem = cartItemIdQueryService.getCartItemById(cartItemId,user);
        return CustomResponse.onSuccess(HttpStatus.OK, cartItem);
    }

    @GetMapping("/{cartId}/list")
    @Operation(summary = "장바구니 목록 조회", description = "장바구니 목록을 조회합니다. 장바구니 목록 조회에 사용되는 API입니다.")
    public CustomResponse<List<CartItemResponseDTO.CartItemListResponseDTO>> getCartItemList(
            @PathVariable("cartId") UUID cartId,
            @AuthenticationPrincipal GatewayPrincipal user
    ){
        List<CartItemResponseDTO.CartItemListResponseDTO> cartItem = cartItemIdQueryService.getCartItemList(cartId,user);
        return CustomResponse.onSuccess(HttpStatus.OK, cartItem);
    }

    @PatchMapping("/{cartItemId}")
    @Operation(summary = "장바구니 항목, 옵션 수정", description = "장바구니 항목, 옵션을 수정합니다. 장바구니 항목, 옵션 수정에 사용되는 API입니다.")
    public CustomResponse<CartItemResponseDTO.CartItemUpdateResponseDTO> updateCartItem(
            @PathVariable("cartItemId") UUID cartItemId,
            @RequestBody CartItemRequestDTO.CartItemUpdateRequestDTO request,
            @AuthenticationPrincipal GatewayPrincipal user
    ){
        CartItemResponseDTO.CartItemUpdateResponseDTO cartItem = cartItemCommandService.updateCartItem(request, cartItemId, user);
        return CustomResponse.onSuccess(HttpStatus.OK, cartItem);
    }

    @DeleteMapping("/{cartItemId}")
    @Operation(summary = "장바구니 항목 삭제", description = "장바구니 항목을 삭제합니다. 장바구니 항목 삭제에 사용되는 API입니다.")
    public CustomResponse<String> deleteCartItem(
            @PathVariable("cartItemId") UUID cartItemId,
            @AuthenticationPrincipal GatewayPrincipal user
    ) {
        cartItemCommandService.deleteCartItem(cartItemId, user);
        return CustomResponse.onSuccess(HttpStatus.OK, "장바구니 항목 삭제 완료");
    }
}
