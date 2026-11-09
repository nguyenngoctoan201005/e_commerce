package com.example.e_commerce.service;

import com.example.e_commerce.dto.request.order.CreateOrderRequest;
import com.example.e_commerce.dto.request.order.UpdateShippingCodeRequest;
import com.example.e_commerce.dto.response.order.MarketingOrderDetailResponse;
import com.example.e_commerce.dto.response.order.MarketingOrderSummaryResponse;
import com.example.e_commerce.dto.response.order.OrderResponse;
import com.example.e_commerce.dto.response.order.OrderSummaryResponse;
import com.example.e_commerce.enums.OrderStatus;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

public interface OrderService {
    OrderResponse createOrderFromCart(String customerId, CreateOrderRequest request);

    Page<OrderResponse> getCustomerOrders(
            String customerId,
            OrderStatus status,
            Pageable pageable);

    OrderResponse getOrderDetailForCustomer(Long orderId, String customerId);

    OrderResponse cancelOrderByCustomer(Long orderId, String customerId);

    List<OrderSummaryResponse> findOrdersByProvinceId(Long provinceId);

    OrderResponse confirmOrder(String employeeId, Long orderId);

    OrderSummaryResponse updateStatusOrder(Long orderId, OrderStatus newStatus);

    OrderResponse getOrderDetailForEmployee(Long orderId, String employeeId);

    Page<OrderResponse> getOrders(Pageable pageable);

    Page<OrderResponse> getOrdersByEmployeeProvinces(String employeeId, OrderStatus status, Pageable pageable);

    OrderResponse getOrderDetailByEmployeeProvinces(Long orderId, String employeeId);

    OrderResponse updateShippingCode(Long orderId, String employeeId, UpdateShippingCodeRequest request);

    Page<MarketingOrderSummaryResponse> getOrdersForMarketing(OrderStatus status, Pageable pageable);

    MarketingOrderDetailResponse getOrderDetailForMarketing(Long orderId);
}