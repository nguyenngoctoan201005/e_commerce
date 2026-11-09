package com.example.e_commerce.dto.response.warehouse;

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
public class ImportNoteResponse {
    Long receiptId;
    LocalDateTime importDate;
    String supplier;
    Long warehouseId;
    String warehouseName;
    String employeeId;
    String employeeName;
    Long totalQuantity;
    BigDecimal totalAmount;
    List<ImportNoteItemResponse> items;
}

