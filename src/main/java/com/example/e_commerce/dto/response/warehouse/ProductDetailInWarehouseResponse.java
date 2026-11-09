package com.example.e_commerce.dto.response.warehouse;

import com.example.e_commerce.dto.response.location.ProductInLocationResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDetailInWarehouseResponse {
    Long productId;
    String productName;
    Long totalPosition;
    Long totalQuantity;
    List<ProductInLocationResponse> productList;
}
