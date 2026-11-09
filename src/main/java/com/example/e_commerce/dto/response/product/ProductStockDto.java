package com.example.e_commerce.dto.response.product;

public class ProductStockDto {
    private Long productId;
    private Long totalStock;

    public ProductStockDto(Long productId, Long totalStock) {
        this.productId = productId;
        this.totalStock = (totalStock != null) ? totalStock : 0L;
    }
}
