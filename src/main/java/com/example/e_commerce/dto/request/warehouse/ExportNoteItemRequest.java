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
public class ExportNoteItemRequest {
    @NotNull(message = "ID sản phẩm không được để trống")
    Long productId;

    @NotNull(message = "ID vị trí xuất không được để trống")
    Long locationId;

    @NotNull(message = "Số lượng xuất không được để trống")
    @Min(value = 1, message = "Số lượng xuất phải lớn hơn 0")
    Long quantity;

    @DecimalMin(value = "0.0", inclusive = true, message = "Giá xuất không được âm")
    BigDecimal exportPrice;
}

