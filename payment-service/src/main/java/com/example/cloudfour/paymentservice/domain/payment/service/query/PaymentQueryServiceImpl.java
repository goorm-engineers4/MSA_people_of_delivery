package com.example.cloudfour.paymentservice.domain.payment.service.query;

import com.example.cloudfour.peopleofdelivery.domain.order.entity.Order;
import com.example.cloudfour.peopleofdelivery.domain.payment.dto.PaymentResponseDTO;
import com.example.cloudfour.peopleofdelivery.domain.payment.entity.Payment;
import com.example.cloudfour.peopleofdelivery.domain.payment.enums.PaymentStatus;
import com.example.cloudfour.peopleofdelivery.domain.payment.exception.PaymentErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.payment.repository.PaymentRepository;
import com.example.cloudfour.peopleofdelivery.domain.store.entity.Store;
import com.example.cloudfour.peopleofdelivery.domain.store.exception.StoreErrorCode;
import com.example.cloudfour.peopleofdelivery.domain.store.exception.StoreException;
import com.example.cloudfour.peopleofdelivery.domain.store.repository.StoreRepository;
import com.example.cloudfour.peopleofdelivery.global.apiPayLoad.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentQueryServiceImpl implements PaymentQueryService {

    private final PaymentRepository paymentRepository;
    private final StoreRepository storeRepository;

    @Override
    public PaymentResponseDTO.PaymentDetailResponseDTO getDetailPayment(UUID orderId, UUID userId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new CustomException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        validateUserAccess(payment.getOrder(), userId);

        return toDetailResponse(payment);
    }

    @Override
    public PaymentResponseDTO.PaymentUserListResponseDTO getUserListPayment(UUID userId) {
        List<Payment> payments = paymentRepository.findAllByOrder_User_Id(userId);

        List<PaymentResponseDTO.PaymentDetailResponseDTO> list = payments.stream()
                .map(this::toDetailResponse)
                .collect(Collectors.toList());

        return PaymentResponseDTO.PaymentUserListResponseDTO.builder()
                .paymentList(list)
                .build();
    }

    @Override
    public PaymentResponseDTO.PaymentStoreListResponseDTO getStoreListPayment(UUID storeId, UUID userId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(StoreErrorCode.NOT_FOUND));

        if (!store.getUser().getId().equals(userId)) {
            throw new StoreException(StoreErrorCode.UNAUTHORIZED_ACCESS);
        }

        List<Payment> payments = paymentRepository.findAllByOrder_Store_Id(storeId);

        List<PaymentResponseDTO.PaymentDetailResponseDTO> list = payments.stream()
                .map(this::toDetailResponse)
                .collect(Collectors.toList());

        return PaymentResponseDTO.PaymentStoreListResponseDTO.builder()
                .paymentList(list)
                .build();
    }

    @Override
    public PaymentResponseDTO.PaymentStoreSummaryResponseDTO getStoreSummaryPayment(UUID storeId, UUID userId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(StoreErrorCode.NOT_FOUND));

        if (!store.getUser().getId().equals(userId)) {
            throw new StoreException(StoreErrorCode.UNAUTHORIZED_ACCESS);
        }

        List<Payment> payments = paymentRepository.findAllByOrder_Store_Id(storeId);

        int totalSales = payments.stream()
                .filter(p -> p.getPaymentStatus() == PaymentStatus.APPROVED)
                .mapToInt(Payment::getTotalPrice)
                .sum();

        int count = (int) payments.stream()
                .filter(p -> p.getPaymentStatus() == PaymentStatus.APPROVED)
                .count();

        return PaymentResponseDTO.PaymentStoreSummaryResponseDTO.builder()
                .totalSales(totalSales)
                .totalCount(count)
                .build();
    }

    private void validateUserAccess(Order order, UUID userId) {
        if (!order.getUser().getId().equals(userId) && !order.getStore().getUser().getId().equals(userId)) {
            throw new CustomException(PaymentErrorCode.UNAUTHORIZED_PAYMENT_ACCESS);
        }
    }

    private PaymentResponseDTO.PaymentDetailResponseDTO toDetailResponse(Payment payment) {
        return PaymentResponseDTO.PaymentDetailResponseDTO.builder()
                .paymentKey(payment.getPaymentKey())
                .orderId(payment.getTossOrderId())
                .amount(payment.getTotalPrice())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .failReason(payment.getFailedReason())
                .build();
    }
}
