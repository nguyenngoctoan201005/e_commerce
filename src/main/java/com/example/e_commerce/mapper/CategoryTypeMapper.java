package com.example.e_commerce.mapper;

import com.example.e_commerce.dto.request.CategoryTypeRequest;
import com.example.e_commerce.dto.response.CategoryTypeResponse;
import com.example.e_commerce.entity.CategoryType;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CategoryTypeMapper {

    CategoryTypeResponse toCategoryTypeResponse(CategoryType categoryType);

    CategoryType toCategoryType(CategoryTypeRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCategoryType(CategoryTypeRequest request, @MappingTarget CategoryType categoryType );
}
