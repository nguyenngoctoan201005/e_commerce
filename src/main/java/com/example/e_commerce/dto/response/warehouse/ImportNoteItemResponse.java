package com.example.e_commerce.dto.response.warehouse;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImportNoteItemResponse {
    Long receiptDetailId;
    Long productId;
    String productName;
    Long locationId;
    String locationName;
    Long quantity;
    BigDecimal importPrice;
    BigDecimal totalPrice;
}

