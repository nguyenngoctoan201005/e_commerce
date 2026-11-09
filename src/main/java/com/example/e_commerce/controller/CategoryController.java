package com.example.e_commerce.controller;

import com.example.e_commerce.dto.request.ApiResponse;
import com.example.e_commerce.dto.request.CategoryRequest;
import com.example.e_commerce.dto.response.CategoryResponse;
import com.example.e_commerce.mapper.CategoryMapper;
import com.example.e_commerce.service.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {
    CategoryService categoryService;
    CategoryMapper categoryMapper;

    @PostMapping
    @PreAuthorize("hasAuthority('CATEGORY_CREATE') or hasRole('ADMIN')")
    public ApiResponse<CategoryResponse> createCategory(@RequestBody CategoryRequest request) {
        CategoryResponse categoryResponse = categoryService.createCategory(request);
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryResponse)
                .build();
    }

    @GetMapping
    public ApiResponse<List<CategoryResponse>> getCategories() {
        return ApiResponse.<List<CategoryResponse>>builder()
                .result(categoryService.getCategories())
                .build();
    }

    @GetMapping("/{categoryId}")
    public ApiResponse<CategoryResponse> getCategory(@PathVariable Long categoryId) {
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.getCategory(categoryId))
                .build();
    }

    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasAuthority('CATEGORY_DELETE') or hasRole('ADMIN')")
    public ApiResponse<String> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ApiResponse.<String>builder()
                .result("Category has been deleted")
                .build();
    }

    @PutMapping("/{categoryId}")
    @PreAuthorize("hasAuthority('CATEGORY_UPDATE') or hasRole('ADMIN')")
    public ApiResponse<CategoryResponse> updateCategory(@PathVariable Long categoryId,
            @RequestBody CategoryRequest request) {
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.updateCategory(categoryId, request))
                .build();
    }
}
