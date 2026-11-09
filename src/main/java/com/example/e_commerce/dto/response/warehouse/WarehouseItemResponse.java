package com.example.e_commerce.dto.response.warehouse;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WarehouseItemResponse {
    Long locationDetailId;
    Long locationId;
    String locationName;
    Long productId;
    String productName;
    Long stockQuantity;
}
