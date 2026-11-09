package com.example.e_commerce.dto.request.warehouse;

import com.example.e_commerce.enums.WarehouseStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WarehouseUpdateRequest {
    @NotBlank(message = "Tên kho không được để trống")
    String warehouseName;

    @NotNull(message = "Vui lòng chọn Tỉnh/Thành phố")
    Long provinceId;

    @NotBlank(message = "Vui lòng gán Nhân viên quản lý kho")
    String managerEmployeeId;

    WarehouseStatus status;
}
