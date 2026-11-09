package com.example.e_commerce.dto.request.product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductFilterRequest {
    private String name;
    private Long minPrice;
    private String categoryName;

    private int page = 0;
    private int size = 20;
    private String sortBy = "productId";
    private String sortDir = "asc";
}
