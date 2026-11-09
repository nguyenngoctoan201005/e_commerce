package com.example.e_commerce.dto.request.warehouse;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MoveProductRequest {
    Long productId;

    @NotNull(message = "Vị trí cũ không được trống")
    Long oldLocationId;

    @NotNull(message = "Vị trí mới không được trống")
    Long newLocationId;

    @NotNull(message = "Số lượng chuyển không được để trống")
    @Min(value = 1, message = "Số lượng chuyển phải lớn hơn 0")
    Long quantity;
}
