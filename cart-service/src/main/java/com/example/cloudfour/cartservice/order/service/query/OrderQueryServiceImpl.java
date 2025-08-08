package com.example.cloudfour.cartservice.order.service.query;

import com.example.cloudfour.cartservice.commondto.MenuOptionResponseDTO;
import com.example.cloudfour.cartservice.commondto.StoreResponseDTO;
import com.example.cloudfour.cartservice.commondto.UserResponseDTO;
import com.example.cloudfour.cartservice.config.GatewayPrincipal;
import com.example.cloudfour.cartservice.order.converter.OrderConverter;
import com.example.cloudfour.cartservice.order.converter.OrderItemConverter;
import com.example.cloudfour.cartservice.order.dto.OrderItemResponseDTO;
import com.example.cloudfour.cartservice.order.dto.OrderResponseDTO;
import com.example.cloudfour.cartservice.order.entity.Order;
import com.example.cloudfour.cartservice.order.entity.OrderItem;
import com.example.cloudfour.cartservice.order.exception.OrderErrorCode;
import com.example.cloudfour.cartservice.order.exception.OrderException;
import com.example.cloudfour.cartservice.order.repository.OrderItemRepository;
import com.example.cloudfour.cartservice.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderQueryServiceImpl {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private static final LocalDateTime first_cursor = LocalDateTime.now().plusDays(1);
    private final RestTemplate restTemplate;

    public OrderResponseDTO.OrderDetailResponseDTO getOrderById(UUID orderId , GatewayPrincipal user) {
        Order order = orderRepository.findById(orderId).orElseThrow(()->new OrderException(OrderErrorCode.NOT_FOUND));
        if(!orderRepository.existsByOrderIdAndUserId(orderId, user.userId())) {
            throw new OrderException(OrderErrorCode.UNAUTHORIZED_ACCESS);
        }
        log.info("주문 조회 권한 확인");
        List<OrderItem> orderItems =  orderItemRepository.findByOrderId(orderId);
        List<OrderItemResponseDTO.OrderItemListResponseDTO> orderItemDTOS =
                orderItems.stream().map(orderItem -> {
                    MenuOptionResponseDTO menuOptionDTO = restTemplate.getForObject(
                            "http://menu-service/api/menus/options/{optionId}/detail",
                            MenuOptionResponseDTO.class,
                            orderItem.getMenuOption()
                    );
                    return OrderItemConverter.toOrderItemClassListDTO(orderItem, menuOptionDTO);
                }).toList();
        log.info("주문 조회 완료");
        return OrderConverter.toOrderDetailResponseDTO(order,orderItemDTOS);
    }

    public OrderResponseDTO.OrderUserListResponseDTO getOrderListByUser(GatewayPrincipal user, LocalDateTime cursor, Integer size) {
        if(cursor == null) {
            cursor = first_cursor;
        }
        Pageable pageable = PageRequest.of(0, size);
        Slice<Order> orders = orderRepository.findAllByUserId(user.userId(),cursor,pageable);
        if(orders.isEmpty()) {
            throw new OrderException(OrderErrorCode.NOT_FOUND);
        }
        log.info("사용자 주문 목록 조회 권한 확인");
        List<Order> orderList = orders.toList();
        List<OrderResponseDTO.OrderUserResponseDTO> orderUserResponseDTOS = orderList.stream().map(order->{
            StoreResponseDTO store = restTemplate.getForObject("http://store-service/api/stores/{storeId}", StoreResponseDTO.class, order.getStore());
            return OrderConverter.toOrderUserResponseDTO(order, store.getName());
        }).toList();
        LocalDateTime next_cursor = null;
        if(!orderList.isEmpty() && orders.hasNext()) {
            next_cursor = orderList.getLast().getCreatedAt();
        }
        log.info("사용자 주문 목록 조회 완료");
        return OrderConverter.toOrderUserListResponseDTO(orderUserResponseDTOS,orders.hasNext(),next_cursor);
    }

    public OrderResponseDTO.OrderStoreListResponseDTO getOrderListByStore(UUID storeId, LocalDateTime cursor, Integer size, GatewayPrincipal user) {
//        if(!storeRepository.existsByStoreAndUser(storeId, user.getId())) {
//            throw new OrderException(OrderErrorCode.UNAUTHORIZED_ACCESS);
//        }
        if(cursor == null) {
            cursor = first_cursor;
        }
        Pageable pageable = PageRequest.of(0, size);
        Slice<Order> orders = orderRepository.findAllByStoreId(storeId,cursor,pageable);
        if(orders.isEmpty()) {
            throw new OrderException(OrderErrorCode.NOT_FOUND);
        }
        log.info("가게 주문 목록 조회 권한 확인");
        List<Order> orderList = orders.toList();
        List<OrderResponseDTO.OrderStoreResponseDTO> orderStoreResponseDTOS = orderList.stream().map(order->{
            UserResponseDTO findUser = restTemplate.getForObject("http://user-service/api/users/{userId}",UserResponseDTO.class,order.getUser());
            return OrderConverter.toOrderStoreResponseDTO(order,findUser.getNickname());
                }).toList();
        LocalDateTime next_cursor = null;
        if(!orderList.isEmpty() && orders.hasNext()) {
            next_cursor = orderList.getLast().getCreatedAt();
        }
        log.info("가게 주문 목록 조회 완료");
        return OrderConverter.toOrderStoreListResponseDTO(orderStoreResponseDTOS,orders.hasNext(),next_cursor);
    }
}
