package com.example.e_commerce.dto.response;

import com.example.e_commerce.entity.Product;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryResponse {
    Long categoryId;
    String categoryName;
    String origin;
}
