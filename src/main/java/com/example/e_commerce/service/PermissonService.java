package com.example.e_commerce.service;

import com.example.e_commerce.dto.response.permission.PermissionResponse;
import com.example.e_commerce.entity.Permission;
import com.example.e_commerce.repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissonService {
    PermissionRepository permissionRepository;


    public List<PermissionResponse> getPermissions() {
        List<Permission> list = permissionRepository.findAll();
        return list.stream().map(permission -> PermissionResponse.builder()
                .permissionId(permission.getPermissionId())
                .permissionName(permission.getPermissionName())
                .build()).toList();
    }



}
