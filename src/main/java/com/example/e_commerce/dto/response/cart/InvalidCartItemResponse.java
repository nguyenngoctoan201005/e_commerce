package com.example.e_commerce.dto.response.cart;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvalidCartItemResponse {
    Long productId;
    String productName;
    Long requestedQuantity;
    Long availableStock;
}
