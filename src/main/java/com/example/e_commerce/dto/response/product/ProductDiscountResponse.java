package com.example.e_commerce.dto.response.product;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDiscountResponse {
    Long productId;
    Long discountId;
    BigDecimal discountedPrice;
    Long applicableQuantity;
    Boolean isActive;
}
