package com.example.e_commerce.dto.request.order;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateShippingCodeRequest {
    @NotBlank(message = "Mã vận chuyển không được để trống")
    String shippingCode;

    @Builder.Default
    Boolean updateToShippingStatus = true;
}

