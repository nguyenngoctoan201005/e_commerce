package com.example.e_commerce.controller;

import com.example.e_commerce.dto.request.ApiResponse;
import com.example.e_commerce.dto.request.CategoryTypeRequest;
import com.example.e_commerce.dto.response.CategoryTypeResponse;
import com.example.e_commerce.service.CategoryTypeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category-types")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryTypeController {

    CategoryTypeService categoryTypeService;

    @PostMapping
    @PreAuthorize("hasAuthority('CATEGORY_CREATE') or hasRole('ADMIN')")
    public ApiResponse<CategoryTypeResponse> categoryType(@RequestBody CategoryTypeRequest request) {
        CategoryTypeResponse categoryTypeResponse = categoryTypeService.createCategoryType(request);
        return ApiResponse.<CategoryTypeResponse>builder()
                .result(categoryTypeResponse)
                .build();
    }

    @GetMapping("/{categoryTypeId}")
    public ApiResponse<CategoryTypeResponse> getCategoryType(@PathVariable Long categoryTypeId) {
        CategoryTypeResponse categoryTypeResponse = categoryTypeService.getCategoryType(categoryTypeId);
        return ApiResponse.<CategoryTypeResponse>builder()
                .result(categoryTypeResponse)
                .build();
    }

    @GetMapping
    public ApiResponse<List<CategoryTypeResponse>> getCategoryTypes() {
        List<CategoryTypeResponse> categoryTypeResponses = categoryTypeService.getCategoryTypes();
        return ApiResponse.<List<CategoryTypeResponse>>builder()
                .result(categoryTypeResponses)
                .build();
    }

    @PutMapping("/{categoryTypeId}")
    @PreAuthorize("hasAuthority('CATEGORY_UPDATE') or hasRole('ADMIN')")
    public ApiResponse<CategoryTypeResponse> updateCategoryType(@PathVariable Long categoryTypeId,
            @RequestBody CategoryTypeRequest request) {
        CategoryTypeResponse categoryTypeResponse = categoryTypeService.updateCategoryType(categoryTypeId, request);
        return ApiResponse.<CategoryTypeResponse>builder()
                .result(categoryTypeResponse)
                .build();
    }

    @DeleteMapping("/{categoryTypeId}")
    @PreAuthorize("hasAuthority('CATEGORY_DELETE') or hasRole('ADMIN')")
    public ApiResponse<String> deleteCategoryType(@PathVariable Long categoryTypeId) {
        String result = categoryTypeService.deleteCategoryType(categoryTypeId);
        return ApiResponse.<String>builder()
                .result(result)
                .build();
    }

}
