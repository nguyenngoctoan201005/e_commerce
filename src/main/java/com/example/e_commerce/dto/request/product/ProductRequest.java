package com.example.e_commerce.dto.request.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductRequest {
    @NotBlank(message = "Tên sản phẩm không được để trống")
    String name;

    @NotNull(message = "Giá sản phẩm không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá sản phẩm phải lớn hơn 0")
    BigDecimal unitPrice;

    String unit;

    @DecimalMin(value = "0.0", inclusive = true, message = "Khối lượng không được âm")
    BigDecimal weight;

    String description;

    @Builder.Default
    Set<String> productImages = new HashSet<>();

    @NotNull(message = "Vui lòng chọn danh mục sản phẩm")
    Long categoryId;

    Long typeId;
}
