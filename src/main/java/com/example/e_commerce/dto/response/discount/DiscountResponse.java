package com.example.e_commerce.dto.response.discount;

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
public class DiscountResponse {
    Long discountId;
    String description;
    BigDecimal discountAmount;
    LocalDateTime startDate;
    LocalDateTime endDate;
    Boolean isActive;
    String status;
    Integer totalApplicableProducts;
    List<Long> applicableProductIds;
}

