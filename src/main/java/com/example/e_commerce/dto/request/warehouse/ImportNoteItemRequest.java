package com.example.e_commerce.dto.request.warehouse;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImportNoteItemRequest {
    @NotNull(message = "ID sản phẩm không được để trống")
    Long productId;

    @NotNull(message = "ID vị trí nhập không được để trống")
    Long locationId;

    @NotNull(message = "Số lượng nhập không được để trống")
    @Min(value = 1, message = "Số lượng nhập phải lớn hơn 0")
    Long quantity;

    @DecimalMin(value = "0.0", inclusive = true, message = "Giá nhập không được âm")
    BigDecimal importPrice;
}

