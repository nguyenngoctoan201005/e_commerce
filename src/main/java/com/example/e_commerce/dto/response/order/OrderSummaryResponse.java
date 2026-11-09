package com.example.e_commerce.dto.response.order;

import com.example.e_commerce.enums.OrderStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderSummaryResponse {
    Long orderId;
    String customerId;
    String recipientName;
    LocalDateTime orderDate;
    OrderStatus status;
    BigDecimal totalAmount;
}
