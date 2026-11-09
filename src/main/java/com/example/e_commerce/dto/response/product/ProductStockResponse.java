package com.example.e_commerce.dto.response.product;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductStockResponse {
    Long productId;
    Long totalStock;
}
