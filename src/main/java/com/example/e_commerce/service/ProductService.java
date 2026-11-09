package com.example.e_commerce.service;

import com.example.e_commerce.dto.request.product.ProductFilterRequest;
import com.example.e_commerce.dto.request.product.ProductRequest;
import com.example.e_commerce.dto.response.product.ProductResponse;
import com.example.e_commerce.entity.Product;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ProductService {
    public ProductResponse createProduct(ProductRequest request);

    public ProductResponse updateProduct(Long id, ProductRequest request);

    public ProductResponse getProduct(Long id);

    public Page<ProductResponse> getProductByCategory(Long categoryId, ProductFilterRequest filter);

    public Page<ProductResponse> getProductByCategoryType(Long typeId, ProductFilterRequest filter);

    public Page<ProductResponse> getProducts(ProductFilterRequest filter);

    public Product getProductById(Long id);

    public Map<Long, Product> getProductsByIds(List<Long> productIds);

    public Map<Long, BigDecimal> productPriceMap (List<Long> productIds);
}
