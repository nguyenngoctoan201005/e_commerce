package com.example.e_commerce.dto.response.permission;

import com.example.e_commerce.enums.UserPermission;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionResponse {
    Long permissionId;
    UserPermission permissionName;
}
