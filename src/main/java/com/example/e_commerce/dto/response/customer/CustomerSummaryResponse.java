package com.example.e_commerce.dto.response.customer;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerSummaryResponse {
    String customerId;
    String fullname;
    String email;
    Long totalOrders;
    BigDecimal totalSpent;
}
