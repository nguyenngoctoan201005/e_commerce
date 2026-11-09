package com.example.e_commerce.dto.request.location;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateLocationRequest {
    @NotNull(message = "Vui lòng chọn Kho")
    Long warehouseId;

    @NotBlank(message = "Tên vị trí không được để trống")
    String locationName;

    String description;
}
