package com.example.e_commerce.service.impl;

import com.example.e_commerce.dto.request.product.ProductFilterRequest;
import com.example.e_commerce.dto.request.product.ProductRequest;
import com.example.e_commerce.dto.response.product.ProductResponse;
import com.example.e_commerce.entity.Category;
import com.example.e_commerce.entity.CategoryType;
import com.example.e_commerce.entity.Product;
import com.example.e_commerce.entity.ProductImage;
import com.example.e_commerce.enums.ErrorCode;
import com.example.e_commerce.exception.AppException;
import com.example.e_commerce.mapper.ProductMapper;
import com.example.e_commerce.repository.ProductRepository;
import com.example.e_commerce.service.*;
import com.example.e_commerce.specification.ProductSpecifications;
import org.springframework.transaction.annotation.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductServiceImpl implements ProductService {
    ProductRepository productRepository;
    ProductMapper productMapper;
    CategoryService categoryService;
    CategoryTypeService categoryTypeService;

    @Transactional
    @Override
    public ProductResponse createProduct(ProductRequest request) {
        Product product = new Product();
        product.setCategory(categoryService.getCategoryById(request.getCategoryId()));
        product.setCategoryType(categoryTypeService.getCategoryTypeById(request.getTypeId()));

        if (request.getProductImages() != null) {
            for (String url : request.getProductImages()) {
                ProductImage productImage = new ProductImage();
                productImage.setImageUrl(url);
                product.addProductImage(productImage);
            }
        }
        productMapper.updAndCreProduct(product, request);
        productRepository.save(product);

        return productMapper.toProductResponse(product);
    }

    @Transactional
    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        if (request.getCategoryId() != null) {
            Category category = categoryService.getCategoryById(request.getCategoryId());
            product.setCategory(category);
        }

        if (request.getTypeId() != null) {
            CategoryType categoryType = categoryTypeService.getCategoryTypeById(request.getTypeId());
            product.setCategoryType(categoryType);
        }

        if (request.getProductImages() != null) {
            product.clearProductImage();
            for (String url : request.getProductImages()) {
                ProductImage productImage = new ProductImage();
                productImage.setImageUrl(url);
                product.addProductImage(productImage);
            }
        }

        productMapper.updAndCreProduct(product, request);

        return productMapper.toProductResponse(product);
    }

    @Override
    public ProductResponse getProduct(Long id) {
        Product product = productRepository
                .findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
        return productMapper.toProductResponse(product);
    }


    @Override
    public Page<ProductResponse> getProductByCategory(Long categoryId, ProductFilterRequest filter) {
        Sort sort = filter.getSortDir().equalsIgnoreCase("desc")
                ? Sort.by(filter.getSortBy()).descending()
                : Sort.by(filter.getSortBy()).ascending();

        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);

        Specification<Product> spec = ProductSpecifications.buildSpec(null, categoryId, filter);

        Page<Product> productPage = productRepository.findAll(spec, pageable);

        Page<ProductResponse> responses = productPage.map(productMapper::toProductResponse);

        return responses;
    }

    @Override
    public Page<ProductResponse> getProductByCategoryType(Long typeId, ProductFilterRequest filter) {
        Sort sort = filter.getSortDir().equalsIgnoreCase("desc")
                ? Sort.by(filter.getSortBy()).descending()
                : Sort.by(filter.getSortBy()).ascending();

        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);

        Specification<Product> spec = ProductSpecifications.buildSpec(typeId, null, filter);

        Page<Product> productPage = productRepository.findAll(spec, pageable);

        Page<ProductResponse> responses = productPage.map(productMapper::toProductResponse);

        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProducts(ProductFilterRequest filter) {

        Sort sort = filter.getSortDir().equalsIgnoreCase("desc")
                ? Sort.by(filter.getSortBy()).descending()
                : Sort.by(filter.getSortBy()).ascending();

        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);

        Specification<Product> spec = ProductSpecifications.buildSpec(null, null, filter);

        Page<Product> productPage = productRepository.findAll(spec, pageable);

        Page<ProductResponse> responses = productPage.map(productMapper::toProductResponse);

        return responses;
    }

    @Override
    public Product getProductById(Long id) {
        Product product = productRepository
                .findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        return product;
    }


    @Override
    public Map<Long, BigDecimal> productPriceMap(List<Long> productIds) {
        List<ProductRepository.ProductPriceProjection> projections = productRepository.findPricesByProductIds(productIds);
        Map<Long, BigDecimal> productPriceMap = projections.stream()
                .collect(Collectors.toMap(
                        ProductRepository.ProductPriceProjection::getProductId,
                        ProductRepository.ProductPriceProjection::getUnitPrice
                ));

        return productPriceMap;
    }

    @Override
    public Map<Long, Product> getProductsByIds(List<Long> productIds){
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Product> products = productRepository.findByProductIdIn(productIds);

        return products.stream()
                .collect(Collectors.toMap(
                        Product::getProductId,
                        Function.identity(),
                        (existing, replacement) -> existing
                ));
    }

}
