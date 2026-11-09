package com.example.e_commerce.dto.response.role;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleSummaryResponse {
    Long roleId;
    String roleName;
    Long totalPermission;
    Long totalEmployeeHaveRole;
}
