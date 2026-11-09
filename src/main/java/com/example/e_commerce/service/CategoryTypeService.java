package com.example.e_commerce.service;

import com.example.e_commerce.dto.request.CategoryRequest;
import com.example.e_commerce.dto.request.CategoryTypeRequest;
import com.example.e_commerce.dto.response.CategoryResponse;
import com.example.e_commerce.dto.response.CategoryTypeResponse;
import com.example.e_commerce.entity.CategoryType;
import com.example.e_commerce.enums.ErrorCode;
import com.example.e_commerce.exception.AppException;
import com.example.e_commerce.mapper.CategoryTypeMapper;
import com.example.e_commerce.repository.CategoryRepository;
import com.example.e_commerce.repository.CategoryTypeRepository;
import com.example.e_commerce.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryTypeService {
    CategoryTypeRepository categoryTypeRepository;
    ProductRepository productRepository;
    CategoryTypeMapper categoryTypeMapper;

    public CategoryTypeResponse createCategoryType(CategoryTypeRequest request){
        CategoryType categoryType = categoryTypeMapper.toCategoryType(request);
        return categoryTypeMapper.toCategoryTypeResponse(categoryTypeRepository.save(categoryType));
    }

    public CategoryTypeResponse getCategoryType(Long id){
        CategoryType categoryType = categoryTypeRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.CATEGORY_TYPE_NOT_EXISTED));
        return categoryTypeMapper.toCategoryTypeResponse(categoryType);
    }

    public List<CategoryTypeResponse> getCategoryTypes(){
        return categoryTypeRepository
                .findAll()
                .stream()
                .map(categoryTypeMapper :: toCategoryTypeResponse)
                .toList();
    }

    @Transactional
    public String deleteCategoryType(Long id){
        if (!categoryTypeRepository.existsById(id)) {
            throw new AppException(ErrorCode.CATEGORY_TYPE_NOT_EXISTED);
        }
        if (productRepository.existsByCategoryType_TypeId(id)) {
            throw new AppException(ErrorCode.CATEGORY_TYPE_HAS_PRODUCTS);
        }
        categoryTypeRepository.deleteById(id);
        return "deleted category type";
    }

    @Transactional
    public CategoryTypeResponse updateCategoryType(Long id, CategoryTypeRequest request) {
        CategoryType categoryType = categoryTypeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_TYPE_NOT_EXISTED));

        categoryTypeMapper.updateCategoryType(request, categoryType);

        CategoryType updatedCategoryType = categoryTypeRepository.save(categoryType);
        return categoryTypeMapper.toCategoryTypeResponse(updatedCategoryType);
    }


    public CategoryType getCategoryTypeById(Long id){
        return categoryTypeRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.CATEGORY_TYPE_NOT_EXISTED));
    }



}
