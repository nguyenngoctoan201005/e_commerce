package com.example.e_commerce.dto.response.order;

import com.example.e_commerce.enums.OrderStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarketingOrderDetailResponse {
    Long orderId;
    LocalDateTime orderDate;
    OrderStatus status;
    String notes;

    String customerId;
    String customerName;
    String recipientName;
    String recipientPhone;
    String email;
    String shippingAddress;
    String districtName;
    String provinceName;

    String paymentMethodName;
    String shippingCode;

    BigDecimal subTotal;
    BigDecimal shippingFee;
    BigDecimal discountAmount;
    BigDecimal totalAmount;

    MarketingDiscountResponse discount;

    List<OrderItemResponse> items;
}

