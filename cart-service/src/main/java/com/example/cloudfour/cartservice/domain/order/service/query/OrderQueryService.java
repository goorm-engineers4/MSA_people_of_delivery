package com.example.cloudfour.cartservice.domain.order.service.query;

import com.example.cloudfour.cartservice.client.StoreClient;
import com.example.cloudfour.cartservice.client.UserClient;
import com.example.cloudfour.cartservice.commondto.MenuOptionResponseDTO;
import com.example.cloudfour.cartservice.commondto.StoreResponseDTO;
import com.example.cloudfour.cartservice.commondto.UserResponseDTO;
import com.example.cloudfour.cartservice.domain.order.converter.OrderConverter;
import com.example.cloudfour.cartservice.domain.order.converter.OrderItemConverter;
import com.example.cloudfour.cartservice.domain.order.dto.OrderItemResponseDTO;
import com.example.cloudfour.cartservice.domain.order.dto.OrderResponseDTO;
import com.example.cloudfour.cartservice.domain.order.entity.Order;
import com.example.cloudfour.cartservice.domain.order.entity.OrderItem;
import com.example.cloudfour.cartservice.domain.order.exception.OrderErrorCode;
import com.example.cloudfour.cartservice.domain.order.exception.OrderException;
import com.example.cloudfour.cartservice.domain.order.exception.OrderItemErrorCode;
import com.example.cloudfour.cartservice.domain.order.exception.OrderItemException;
import com.example.cloudfour.cartservice.domain.order.repository.OrderItemRepository;
import com.example.cloudfour.cartservice.domain.order.repository.OrderRepository;
import com.example.cloudfour.modulecommon.dto.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderQueryService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private static final LocalDateTime first_cursor = LocalDateTime.now().plusDays(1);
    private final StoreClient storeClient;
    private final UserClient userClient;

    public OrderResponseDTO.OrderDetailResponseDTO getOrderById(UUID orderId , CurrentUser user) {
        Order order = orderRepository.findById(orderId).orElseThrow(()->{
            log.warn("존재하지 않는 주문");
            return new OrderException(OrderErrorCode.NOT_FOUND);
        });
        if(user == null || !orderRepository.existsByOrderIdAndUserId(orderId, user.id())) {
            log.warn("주문 조회 권한 없음");
            throw new OrderException(OrderErrorCode.UNAUTHORIZED_ACCESS);
        }
        log.info("주문 조회 권한 확인 성공");
        List<OrderItem> orderItems =  orderItemRepository.findByOrderId(orderId);
        List<OrderItemResponseDTO.OrderItemListResponseDTO> orderItemDTOS =
                orderItems.stream().map(orderItem -> {
                    MenuOptionResponseDTO menuOptionDTO = storeClient.menuOptionById(orderItem.getMenuOption());
                    return OrderItemConverter.toOrderItemClassListDTO(orderItem, menuOptionDTO);
                }).toList();
        log.info("주문 조회 완료");
        return OrderConverter.toOrderDetailResponseDTO(order,orderItemDTOS);
    }

    public OrderItemResponseDTO.OrderItemListResponseDTO getOrderItemById(UUID orderItemId, CurrentUser user){
        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(()->{
            log.warn("존재하지 않는 주문 아이템");
            return new OrderItemException(OrderItemErrorCode.NOT_FOUND);
        });
        if(user == null || orderItemRepository.existsByUserId(orderItem.getId(),user.id())){
            log.warn("주문 아이템 조회 권한 없음");
            throw new OrderItemException(OrderItemErrorCode.UNAUTHORIZED_ACCESS);
        }
        log.info("주문 아이템 조회 권한 확인 성공");
        MenuOptionResponseDTO menuOptionDTO = storeClient.menuOptionById(orderItem.getMenuOption());
        log.info("주문 아이템 조회 완료");
        return OrderItemConverter.toOrderItemClassListDTO(orderItem,menuOptionDTO);
    }

    public OrderResponseDTO.OrderUserListResponseDTO getOrderListByUser(CurrentUser user, LocalDateTime cursor, Integer size) {
        if(user == null){
            log.warn("사용자 주문 목록 조회 권한 없음");
            throw new OrderException(OrderErrorCode.UNAUTHORIZED_ACCESS);
        }
        log.info("사용자 주문 목록 조회 권한 확인 성공");
        if(cursor == null) {
            cursor = first_cursor;
        }
        Pageable pageable = PageRequest.of(0, size);
        Slice<Order> orders = orderRepository.findAllByUserId(user.id(),cursor,pageable);
        if(orders.isEmpty()) {
            log.warn("존재하지 않는 주문");
            throw new OrderException(OrderErrorCode.NOT_FOUND);
        }
        List<Order> orderList = orders.toList();
        List<OrderResponseDTO.OrderUserResponseDTO> orderUserResponseDTOS = orderList.stream().map(order->{
            StoreResponseDTO store = storeClient.storeById(order.getStore());
            return OrderConverter.toOrderUserResponseDTO(order, store.getName());
        }).toList();
        LocalDateTime next_cursor = null;
        if(!orderList.isEmpty() && orders.hasNext()) {
            next_cursor = orderList.getLast().getCreatedAt();
        }
        log.info("사용자 주문 목록 조회 완료");
        return OrderConverter.toOrderUserListResponseDTO(orderUserResponseDTOS,orders.hasNext(),next_cursor);
    }

    public OrderResponseDTO.OrderStoreListResponseDTO getOrderListByStore(UUID storeId, LocalDateTime cursor, Integer size, CurrentUser user) {
        StoreResponseDTO store = storeClient.storeById(storeId);
        if(user == null || store.getUserId() != user.id()) {
            log.info("가게 주문 목록 조회 권한 없음");
            throw new OrderException(OrderErrorCode.UNAUTHORIZED_ACCESS);
        }
        if(cursor == null) {
            cursor = first_cursor;
        }
        Pageable pageable = PageRequest.of(0, size);
        Slice<Order> orders = orderRepository.findAllByStoreId(storeId,cursor,pageable);
        if(orders.isEmpty()) {
            throw new OrderException(OrderErrorCode.NOT_FOUND);
        }
        log.info("가게 주문 목록 조회 권한 확인 성공");
        List<Order> orderList = orders.toList();
        List<OrderResponseDTO.OrderStoreResponseDTO> orderStoreResponseDTOS = orderList.stream().map(order->{
            UserResponseDTO findUser = userClient.userById(order.getUser());
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
