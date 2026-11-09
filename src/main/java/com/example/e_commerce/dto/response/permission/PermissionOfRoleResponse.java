package com.example.e_commerce.dto.response.permission;

import com.example.e_commerce.enums.UserRole;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionOfRoleResponse {
    UserRole roleName;
    List<PermissionResponse> permissionList;
}
