package com.example.e_commerce.dto.response.discount;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PromotionLogResponse {
    Long promLogId;
    String action;
    LocalDateTime modifiedAt;
    Long discountId;
    String discountCode;
    String employeeId;
    String employeeName;
}

