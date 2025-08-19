package com.example.cloudfour.cartservice.order.service.command;

import com.example.cloudfour.cartservice.cart.entity.Cart;
import com.example.cloudfour.cartservice.cart.exception.CartErrorCode;
import com.example.cloudfour.cartservice.cart.exception.CartException;
import com.example.cloudfour.cartservice.cart.repository.CartRepository;
import com.example.cloudfour.cartservice.cartitem.entity.CartItem;
import com.example.cloudfour.cartservice.cartitem.exception.CartItemException;
import com.example.cloudfour.cartservice.cartitem.repository.CartItemRepository;
import com.example.cloudfour.cartservice.commondto.UserAddressResponseDTO;
import com.example.cloudfour.cartservice.order.converter.OrderConverter;
import com.example.cloudfour.cartservice.order.converter.OrderItemConverter;
import com.example.cloudfour.cartservice.order.dto.OrderRequestDTO;
import com.example.cloudfour.cartservice.order.dto.OrderResponseDTO;
import com.example.cloudfour.cartservice.order.entity.Order;
import com.example.cloudfour.cartservice.order.entity.OrderItem;
import com.example.cloudfour.cartservice.order.enums.OrderStatus;
import com.example.cloudfour.cartservice.order.exception.OrderErrorCode;
import com.example.cloudfour.cartservice.order.exception.OrderException;
import com.example.cloudfour.cartservice.order.repository.OrderItemRepository;
import com.example.cloudfour.cartservice.order.repository.OrderRepository;
import com.example.cloudfour.modulecommon.dto.CurrentUser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderCommandService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final RestTemplate restTemplate;


    public OrderResponseDTO.OrderCreateResponseDTO createOrder(OrderRequestDTO.OrderCreateRequestDTO orderCreateRequestDTO, UUID cartId, CurrentUser user) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(()->{
            log.warn("존재하지 않는 장바구니");
            return new CartException(CartErrorCode.NOT_FOUND);
        });
        if(user == null || !cartRepository.existsByUserAndCart(user.id(), cartId)){
            log.warn("주문 생성 권한 없음");
            throw new OrderException(OrderErrorCode.UNAUTHORIZED_ACCESS);
        }
        UserAddressResponseDTO userAddress = restTemplate.getForObject("http://user-service/api/users/{userId}",UserAddressResponseDTO.class, user.id());
        UUID findStore = restTemplate.getForObject("http://store-service/api/stores/{storeId}", UUID.class,cart.getStore());
        List<CartItem> cartItems = cartItemRepository.findAllByCartId(cartId,user.id());
        if(cartItems.isEmpty()) {
            log.warn("존재하지 않는 장바구니 아이템");
            throw new CartItemException(CartErrorCode.NOT_FOUND);
        }
        log.info("주문 생성 권한 확인 성공");
        Integer totalPrice = 0;
        for (CartItem cartItem : cartItems) {
            totalPrice += cartItem.getPrice();
        }
        Order order = OrderConverter.toOrder(orderCreateRequestDTO,totalPrice,userAddress.getAddress());
        order.setStore(findStore);
        order.setUser(user.id());
        orderRepository.save(order);
        log.info("주문 생성 완료. 주문 아이템 생성, 장바구니 삭제 남음");
        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> OrderItemConverter.CartItemtoOrderItem(cartItem, order)).toList();
        orderItemRepository.saveAll(orderItems);
        log.info("주문 아이템 생성 완료. 장바구니 삭제 남음");
        cartRepository.delete(cart);
        log.info("장바구니 삭제 완료.");
        return OrderConverter.toOrderCreateResponseDTO(order);
    }

    public OrderResponseDTO.OrderUpdateResponseDTO updateOrder(OrderRequestDTO.OrderUpdateRequestDTO orderUpdateRequestDTO, UUID orderId, CurrentUser user) {
        if(user == null || !orderRepository.existsByOrderIdAndUserId(orderId, user.id())) {
            log.warn("주문 수정 권한 없음");
            throw new OrderException(OrderErrorCode.UNAUTHORIZED_ACCESS);
        }
        Order order = orderRepository.findById(orderId).orElseThrow(()->{
            log.warn("존재하지 않는 주문");
            return new OrderException(OrderErrorCode.NOT_FOUND);
        });
        log.info("주문 수정 권한 확인 성공");
        OrderStatus prev_orderStatus = order.getStatus();
        order.updateOrderStatus(orderUpdateRequestDTO.getNewStatus());
        orderRepository.save(order);
        log.info("주문 수정 완료");
        return OrderConverter.toOrderUpdateResponseDTO(order,prev_orderStatus);
    }

    public void deleteOrder(UUID orderId, CurrentUser user) {
        if(user == null || !orderRepository.existsByOrderIdAndUserId(orderId, user.id())) {
            log.warn("주문 삭제 권한 없음");
            throw new OrderException(OrderErrorCode.UNAUTHORIZED_ACCESS);
        }
        Order order = orderRepository.findById(orderId).orElseThrow(()->{
            log.warn("존재하지 않는 주문");
            return new OrderException(OrderErrorCode.NOT_FOUND);
        });
        log.info("주문 삭제 권한 확인");
        order.softDelete();
        log.info("주문 삭제 완료");
    }
}
