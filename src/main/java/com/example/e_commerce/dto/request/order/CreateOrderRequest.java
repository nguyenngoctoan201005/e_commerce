package com.example.e_commerce.dto.request.order;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateOrderRequest {
    @NotBlank(message = "Tên người nhận không được để trống")
    String recipientName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0|\\+84)[0-9]{9,10}$", message = "Số điện thoại không đúng định dạng")
    String recipientPhone;

    @Email(message = "Email không đúng định dạng")
    String email;

    @NotBlank(message = "Địa chỉ nhận hàng không được để trống")
    String shippingAddress;

    @NotNull(message = "Vui lòng chọn Quận/Huyện giao hàng")
    Long districtId;

    @NotNull(message = "Vui lòng chọn phương thức thanh toán")
    Long paymentMethodId;

    Long discountId;
    String notes;
}
