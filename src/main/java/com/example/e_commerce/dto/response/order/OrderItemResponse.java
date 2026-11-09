package com.example.e_commerce.dto.response.order;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemResponse {
    String productId;
    String productName;
    String productImage;
    String unit;
    BigDecimal unitPrice;
    Long quantity;
    BigDecimal totalPrice;
}
