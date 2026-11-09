package com.example.e_commerce.dto.response.order;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarketingDiscountResponse {
    Long discountId;
    String discountCode;
    BigDecimal discountAmount;
    LocalDateTime startDate;
    LocalDateTime endDate;
}

