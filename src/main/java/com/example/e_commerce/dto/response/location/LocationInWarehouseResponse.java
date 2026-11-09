package com.example.e_commerce.dto.response.location;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocationInWarehouseResponse {
    Long locationId;
    String locationName;
    String description;

    Long warehouseId;
    String warehouseName;

    Long stockQuantity;
}
