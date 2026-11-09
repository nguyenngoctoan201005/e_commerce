package com.example.e_commerce.dto.response.warehouse;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WarehouseActivityResponse {
    String activityType;
    Long activityId;
    String activityCode;
    LocalDateTime timestamp;
    String description;
    Long warehouseId;
    String warehouseName;
    String employeeId;
    String employeeName;
    Long totalQuantity;
    BigDecimal totalAmount;
    Integer totalProductTypes;
}

