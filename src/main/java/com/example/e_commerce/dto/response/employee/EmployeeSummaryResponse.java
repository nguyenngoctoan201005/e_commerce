package com.example.e_commerce.dto.response.employee;

import com.example.e_commerce.enums.UserRole;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeSummaryResponse {
    String employeeId;
    String fullname;
    String idCardNumber;
    Set<UserRole> roles;
}
