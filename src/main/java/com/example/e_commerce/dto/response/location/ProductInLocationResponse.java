package com.example.e_commerce.dto.response.location;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductInLocationResponse {
    Long locationDetailId;
    Long locationId;
    String locationName;
    String description;
    Long stockQuantity;
}
