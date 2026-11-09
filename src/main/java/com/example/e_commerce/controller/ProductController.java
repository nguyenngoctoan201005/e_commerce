package com.example.e_commerce.controller;

import com.example.e_commerce.dto.request.ApiResponse;
import com.example.e_commerce.dto.request.product.ProductFilterRequest;
import com.example.e_commerce.dto.request.product.ProductRequest;
import com.example.e_commerce.dto.response.product.ProductResponse;
import com.example.e_commerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {
    ProductService productService;

    @PostMapping
    @PreAuthorize("hasAuthority('PRODUCT_CREATE') or hasRole('ADMIN')")
    public ApiResponse<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ApiResponse.<ProductResponse>builder()
                .result(response)
                .build();
    }

    @GetMapping("/{productId}")
    public ApiResponse<ProductResponse> getProduct(@PathVariable Long productId) {
        ProductResponse response = productService.getProduct(productId);
        return ApiResponse.<ProductResponse>builder()
                .result(response)
                .build();
    }

    @GetMapping
    public ApiResponse<Page<ProductResponse>> getProducts(ProductFilterRequest filter) {
        return ApiResponse.<Page<ProductResponse>>builder()
                .result(productService.getProducts(filter))
                .build();
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasAuthority('PRODUCT_UPDATE') or hasRole('ADMIN')")
    public ApiResponse<ProductResponse> updateProduct(@PathVariable Long productId,
            @Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.updateProduct(productId, request);
        return ApiResponse.<ProductResponse>builder()
                .result(response)
                .build();
    }

    @GetMapping("/categories/{categoryId}")
    public ApiResponse<Page<ProductResponse>> getProductByCategory(@PathVariable Long categoryId,
            ProductFilterRequest filter) {
        return ApiResponse.<Page<ProductResponse>>builder()
                .result(productService.getProductByCategory(categoryId, filter))
                .build();
    }

    @GetMapping("/category-types/{typeId}")
    public ApiResponse<Page<ProductResponse>> getProductByCategoryType(@PathVariable Long typeId,
            ProductFilterRequest filter) {
        return ApiResponse.<Page<ProductResponse>>builder()
                .result(productService.getProductByCategoryType(typeId, filter))
                .build();
    }

}
