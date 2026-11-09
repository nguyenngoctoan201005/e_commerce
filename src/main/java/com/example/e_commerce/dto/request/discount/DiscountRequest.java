package com.example.e_commerce.dto.request.discount;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiscountRequest {

    @NotBlank(message = "Mã/mô tả giảm giá không được để trống")
    String description;

    @NotNull(message = "Số tiền giảm giá không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Số tiền giảm giá phải lớn hơn 0")
    BigDecimal discountAmount;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    LocalDateTime startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    LocalDateTime endDate;

    List<Long> productIds;

    Long applicableQuantity;
}

