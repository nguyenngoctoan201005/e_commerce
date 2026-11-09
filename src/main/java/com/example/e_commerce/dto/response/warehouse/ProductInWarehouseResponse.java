package com.example.e_commerce.dto.response.warehouse;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductInWarehouseResponse {
    Long warehouseId;
    Long productId;
    String productName;
    Long totalPosition;
    Long totalQuantity;
}
