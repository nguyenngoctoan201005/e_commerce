package com.example.e_commerce.dto.response.cart;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartSummary {
    BigDecimal totalOriginalPrice;
    BigDecimal totalDiscountAmount;
    BigDecimal totalPayment;
}
