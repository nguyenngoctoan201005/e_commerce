package com.example.e_commerce.dto.response.customer;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerDetailResponse {
    String customerId;
    String email;
    String phoneNumber;
    String fullname;
    String address;
    Long totalOrders;
    BigDecimal totalSpent;
}
