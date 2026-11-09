package com.example.e_commerce.dto.response.employee;

import com.example.e_commerce.dto.response.province.ProvinceResponse;
import com.example.e_commerce.entity.EmployeeProvince;
import com.example.e_commerce.entity.Role;
import com.example.e_commerce.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeDetailResponse {
    private String employeeId;
    private String fullname;
    private String idCardNumber;
    private String username;

    private Set<UserRole> roles;
    private Set<ProvinceResponse> assignedProvinces;
}
