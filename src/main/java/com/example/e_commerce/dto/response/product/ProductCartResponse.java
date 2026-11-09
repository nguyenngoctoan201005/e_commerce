package com.example.e_commerce.dto.response.product;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCartResponse {
    Long productId;
    String productName;
    String imageUrl;
    BigDecimal originalPrice;
}
