package com.example.e_commerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentMethodRequest {
    @NotBlank(message = "Tên phương thức thanh toán không được để trống")
    String paymentMethodName;
}
