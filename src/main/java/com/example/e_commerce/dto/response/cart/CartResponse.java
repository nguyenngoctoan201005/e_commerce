package com.example.e_commerce.dto.response.cart;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartResponse {
    Long cartId;
    String customerId;
    List<CartItemResponse> items;
    CartSummary summary;
}
