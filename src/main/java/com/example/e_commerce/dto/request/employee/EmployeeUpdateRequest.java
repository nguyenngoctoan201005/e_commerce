package com.example.e_commerce.dto.request.employee;

import com.example.e_commerce.enums.UserRole;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeUpdateRequest {
    @NotBlank(message = "Họ và tên không được để trống")
    String fullName;

    @NotBlank(message = "Số CCCD không được để trống")
    @Pattern(regexp = "^\\d{12}$", message = "Số CCCD phải bao gồm đúng 12 chữ số")
    String idCardNumber;

    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 4, max = 30, message = "Tên đăng nhập phải từ 4 đến 30 ký tự")
    String username;

    @Size(min = 6, message = "Mật khẩu phải từ 6 ký tự trở lên")
    String password;

    @NotEmpty(message = "Nhân viên phải được gán ít nhất 1 tỉnh/thành phố")
    Set<Long> provinceIds;

    @NotEmpty(message = "Nhân viên phải được gán ít nhất 1 vai trò")
    Set<UserRole> roles;
}
