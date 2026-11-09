package com.example.e_commerce.dto.request.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerUpdateRequest {
    @Email(message = "Email không đúng định dạng")
    String email;

    @NotBlank(message = "Họ và tên không được để trống")
    String fullname;

    String address;
    String phoneNumber;
    Long districtId;
}
