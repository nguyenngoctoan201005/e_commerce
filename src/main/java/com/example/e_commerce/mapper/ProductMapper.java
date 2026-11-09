package com.example.e_commerce.mapper;

import com.example.e_commerce.dto.request.product.ProductRequest;
import com.example.e_commerce.dto.response.product.ProductResponse;
import com.example.e_commerce.entity.Product;
import com.example.e_commerce.entity.ProductImage;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Named("mapImages")
    default List<String> mapImages(Set<ProductImage> productImages) {
        if (productImages == null) return null;
        return productImages.stream()
                .map(ProductImage::getImageUrl)
                .collect(Collectors.toList());
    }

    @Mapping(source = "category.categoryName", target = "categoryName")
    @Mapping(source = "categoryType.typeName", target = "typeName")
    @Mapping(source = "productImages", target = "imageUrls", qualifiedByName = "mapImages")
    ProductResponse toProductResponse(Product product);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "categoryType", ignore = true)
    @Mapping(target = "productImages", ignore = true)
    void updAndCreProduct(@MappingTarget Product product, ProductRequest request);


}
