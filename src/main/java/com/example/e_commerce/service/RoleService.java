package com.example.e_commerce.service;

import com.example.e_commerce.dto.response.permission.PermissionOfRoleResponse;
import com.example.e_commerce.dto.response.permission.PermissionResponse;
import com.example.e_commerce.dto.response.role.RoleSummaryResponse;
import com.example.e_commerce.entity.Permission;
import com.example.e_commerce.entity.Role;
import com.example.e_commerce.enums.ErrorCode;
import com.example.e_commerce.exception.AppException;
import com.example.e_commerce.repository.PermissionRepository;
import com.example.e_commerce.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.event.PaintEvent;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {

    RoleRepository roleRepository;
    PermissionRepository permissionRepository;


    @Transactional(readOnly = true)
    public List<RoleSummaryResponse> getAllRoleSummaries() {
        return roleRepository.getRoleSummaries();
    }

    public PermissionOfRoleResponse getAllPermissionOfRole(Long roleId){
        Role role = roleRepository.findById(roleId)
                .orElseThrow(()-> new AppException(ErrorCode.ROLE_NOT_EXISTED));

        PermissionOfRoleResponse por = PermissionOfRoleResponse.builder()
                .roleName(role.getRoleName())
                .permissionList(roleRepository.getAllPermissionOfRole(roleId))
                .build();
        return por;
    }

    @Transactional
    public PermissionOfRoleResponse updatePermissionOfRole(Long roleId, Set<Long> permissionIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));

        List<Permission> permissions = permissionRepository.findAllById(permissionIds);

        if (permissions.size() != permissionIds.size()) {
            throw new AppException(ErrorCode.PERMISSION_NOT_EXISTED);
        }

        role.getPermissions().clear();
        role.getPermissions().addAll(permissions);


        return PermissionOfRoleResponse.builder()
                .roleName(role.getRoleName())
                .permissionList(roleRepository.getAllPermissionOfRole(roleId))
                .build();
    }


    @Transactional
    public PermissionOfRoleResponse removePermission(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        role.getPermissions().clear();

        return PermissionOfRoleResponse.builder()
                .roleName(role.getRoleName())
                .permissionList(roleRepository.getAllPermissionOfRole(roleId))
                .build();
    }








    

}
