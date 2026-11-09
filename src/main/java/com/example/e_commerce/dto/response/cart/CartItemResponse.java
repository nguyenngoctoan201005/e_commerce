package com.example.e_commerce.dto.response.cart;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemResponse {
    Long productId;
    String productName;
    String imageUrl;
    BigDecimal originalPrice;
    BigDecimal discountedPrice;
    Long quantity;
    BigDecimal subTotal;
    Boolean isAvailable;
    Long stockAvailable;
}
