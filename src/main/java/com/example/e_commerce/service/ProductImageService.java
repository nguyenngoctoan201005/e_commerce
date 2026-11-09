package com.example.e_commerce.service;

import com.example.e_commerce.entity.ProductImage;
import com.example.e_commerce.repository.ProductImageRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductImageService {
    ProductImageRepository productImageRepository;

    public Map<Long, String> imageMap(List<Long> productIds){
        return productImageRepository.findMainImagesByProductIds(productIds)
                .stream()
                .collect(Collectors.toMap(
                        ProductImageRepository.ProductImageProjection::getProductId,
                        ProductImageRepository.ProductImageProjection::getImageUrl,
                        (oldVal, newVal) -> oldVal
                ));

    }

}
