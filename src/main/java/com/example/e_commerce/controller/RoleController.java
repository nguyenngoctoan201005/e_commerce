package com.example.e_commerce.controller;

import com.example.e_commerce.dto.request.ApiResponse;
import com.example.e_commerce.dto.response.permission.PermissionOfRoleResponse;
import com.example.e_commerce.dto.response.permission.PermissionResponse;
import com.example.e_commerce.dto.response.role.RoleSummaryResponse;
import com.example.e_commerce.entity.Permission;
import com.example.e_commerce.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasAuthority('ROLE_MANAGEMENT') or hasRole('ADMIN')")
public class RoleController {
    RoleService roleService;


    @GetMapping
    public ApiResponse<List<RoleSummaryResponse>> getAllRoleSummaries(){
        return ApiResponse.<List<RoleSummaryResponse>>builder()
                .result(roleService.getAllRoleSummaries())
                .build();
    }

    @GetMapping("/{roleId}")
    public ApiResponse<PermissionOfRoleResponse> getAllPermissionOfRole(@PathVariable Long roleId ){
        return ApiResponse.<PermissionOfRoleResponse>builder()
                .result(roleService.getAllPermissionOfRole(roleId))
                .build();
    }

    @PutMapping("/{roleId}")
    public ApiResponse<PermissionOfRoleResponse> updatePermissionOfRole(@PathVariable Long roleId, @RequestBody Set<Long> permissionIds){
        return ApiResponse.<PermissionOfRoleResponse>builder()
                .result(roleService.updatePermissionOfRole(roleId,permissionIds))
                .build();
    }


    @PutMapping("/remove/{roleId}")
    public ApiResponse<PermissionOfRoleResponse> removePermission(@PathVariable Long roleId){
        return ApiResponse.<PermissionOfRoleResponse>builder()
                .result(roleService.removePermission(roleId))
                .build();
    }
}
