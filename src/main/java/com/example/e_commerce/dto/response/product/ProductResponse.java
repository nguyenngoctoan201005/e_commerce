package com.example.e_commerce.dto.response.product;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {

    Long productId;
    String name;
    BigDecimal unitPrice;
    String unit;
    BigDecimal weight;
    String description;

    String categoryName;
    String typeName;

    List<String> imageUrls;
}