package com.example.e_commerce.dto.response.warehouse;

import com.example.e_commerce.enums.WarehouseStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WarehouseSummaryResponse {
    Long warehouseId;
    String warehouseName;
    WarehouseStatus status;

    String provinceName;

    String employeeId;
    String fullname;

    Long totalLocations ;
    Long totalStockQuantity ;
}
