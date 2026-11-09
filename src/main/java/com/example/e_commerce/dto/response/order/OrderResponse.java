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
public class OrderResponse {

    String orderId;
    LocalDateTime orderDate;
    OrderStatus status;
    String notes;


    String recipientName;
    String recipientPhone;
    String email;
    String shippingAddress;
    String districtName;
    String provinceName;
    String shippingCode;


    String paymentMethodName;
    BigDecimal subTotal;
    BigDecimal shippingFee;
    String discountCode;
    BigDecimal discountAmount;
    BigDecimal totalAmount;


    String employeeName;

    List<OrderItemResponse> items;


}
