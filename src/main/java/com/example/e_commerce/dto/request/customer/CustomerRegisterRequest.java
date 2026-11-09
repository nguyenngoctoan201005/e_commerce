package com.example.e_commerce.dto.request.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerRegisterRequest {
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải từ 6 ký tự trở lên")
    String password;

    @NotBlank(message = "Họ và tên không được để trống")
    String fullname;

    String address;

    @NotBlank(message = "Số điện thoại không được để trống")
    String phoneNumber;

    @NotNull(message = "Vui lòng chọn Quận/Huyện")
    Long districtId;
}
