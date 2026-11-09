package com.example.e_commerce.configuration;

import com.example.e_commerce.entity.Employee;
import com.example.e_commerce.entity.Permission;
import com.example.e_commerce.entity.Role;
import com.example.e_commerce.enums.UserPermission;
import com.example.e_commerce.enums.UserRole;
import com.example.e_commerce.repository.EmployeeRepository;
import com.example.e_commerce.repository.PermissionRepository;
import com.example.e_commerce.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class RolePermissionDataInitializer {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initData() {
        log.info("--- BẮT ĐẦU KHỞI TẠO DỮ LIỆU ROLES & PERMISSIONS ---");

        List<Permission> existingPermissions = permissionRepository.findAll();
        Set<UserPermission> existingNames = existingPermissions.stream()
                .map(Permission::getPermissionName)
                .collect(Collectors.toSet());

        List<Permission> newPermissions = new ArrayList<>();
        for (UserPermission permEnum : UserPermission.values()) {
            if (!existingNames.contains(permEnum)) {
                Permission permission = new Permission();
                permission.setPermissionName(permEnum);
                newPermissions.add(permission);
            }
        }
        if (!newPermissions.isEmpty()) {
            permissionRepository.saveAll(newPermissions);
            permissionRepository.flush();
        }

        List<Permission> allPermissions = permissionRepository.findAll();
        Map<UserPermission, Permission> permissionMap = allPermissions.stream()
                .collect(Collectors.toMap(Permission::getPermissionName, Function.identity()));

        Role adminRole = createOrUpdateRole(UserRole.ADMIN, new HashSet<>(allPermissions));

        Set<Permission> salePermissions = getPermissionsFromMap(permissionMap, Set.of(
                UserPermission.PRODUCT_VIEW,
                UserPermission.CATEGORY_VIEW,
                UserPermission.CUSTOMER_VIEW,
                UserPermission.ORDER_VIEW_ALL,
                UserPermission.ORDER_CREATE,
                UserPermission.ORDER_UPDATE,
                UserPermission.ORDER_APPROVE,
                UserPermission.ORDER_CANCEL,
                UserPermission.WAREHOUSE_VIEW,
                UserPermission.INVENTORY_VIEW,
                UserPermission.REVIEW_VIEW,
                UserPermission.REVIEW_REPLY,
                UserPermission.REPORT_SALES_VIEW
        ));
        createOrUpdateRole(UserRole.SALE, salePermissions);

        Set<Permission> warehousePermissions = getPermissionsFromMap(permissionMap, Set.of(
                UserPermission.PRODUCT_VIEW,
                UserPermission.CATEGORY_VIEW,
                UserPermission.WAREHOUSE_VIEW,
                UserPermission.LOCATION_VIEW,
                UserPermission.LOCATION_MANAGE,
                UserPermission.INVENTORY_VIEW,
                UserPermission.INVENTORY_UPDATE,
                UserPermission.IMPORT_NOTE_CREATE,
                UserPermission.IMPORT_NOTE_APPROVE,
                UserPermission.EXPORT_NOTE_CREATE,
                UserPermission.EXPORT_NOTE_APPROVE,
                UserPermission.ORDER_VIEW_ALL,
                UserPermission.ORDER_APPROVE,
                UserPermission.ORDER_UPDATE,
                UserPermission.REPORT_INVENTORY_VIEW
        ));
        createOrUpdateRole(UserRole.WAREHOUSE, warehousePermissions);

        Set<Permission> marketingPermissions = getPermissionsFromMap(permissionMap, Set.of(
                UserPermission.PRODUCT_VIEW,
                UserPermission.PRODUCT_CREATE,
                UserPermission.PRODUCT_UPDATE,
                UserPermission.CATEGORY_VIEW,
                UserPermission.CATEGORY_CREATE,
                UserPermission.CATEGORY_UPDATE,
                UserPermission.PROMOTION_VIEW,
                UserPermission.PROMOTION_CREATE,
                UserPermission.PROMOTION_UPDATE,
                UserPermission.PROMOTION_DELETE,
                UserPermission.REVIEW_VIEW,
                UserPermission.REVIEW_REPLY,
                UserPermission.REVIEW_MODERATE,
                UserPermission.REPORT_MARKETING_VIEW,
                UserPermission.ORDER_VIEW_ALL,
                UserPermission.CUSTOMER_VIEW
        ));
        createOrUpdateRole(UserRole.MARKETING, marketingPermissions);

        Set<Permission> customerPermissions = getPermissionsFromMap(permissionMap, Set.of(
                UserPermission.PRODUCT_VIEW,
                UserPermission.CATEGORY_VIEW,
                UserPermission.ORDER_VIEW_SELF,
                UserPermission.ORDER_CREATE,
                UserPermission.ORDER_CANCEL
        ));
        createOrUpdateRole(UserRole.CUSTOMER, customerPermissions);

        String adminUsername = "admin";
        if (!employeeRepository.existsByUsername(adminUsername)) {
            Employee admin = Employee.builder()
                    .fullname("System Administrator")
                    .username(adminUsername)
                    .password(passwordEncoder.encode("admin123"))
                    .idCardNumber("000000000000")
                    .roles(Set.of(adminRole))
                    .employeeProvinces(new HashSet<>())
                    .build();

            employeeRepository.save(admin);
            log.info("==> Khởi tạo thành công tài khoản Admin mặc định: [username: admin, password: admin123]");
        }

        log.info("--- KHỞI TẠO DỮ LIỆU BẢO MẬT HOÀN TẤT ---");
    }

    private Role createOrUpdateRole(UserRole roleName, Set<Permission> permissions) {
        Role role = roleRepository.findByRoleName(roleName)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setRoleName(roleName);
                    return newRole;
                });

        role.setPermissions(permissions);
        return roleRepository.save(role);
    }

    private Set<Permission> getPermissionsFromMap(Map<UserPermission, Permission> permissionMap,
            Set<UserPermission> targetPermissions) {
        return targetPermissions.stream()
                .map(permissionMap::get)
                .filter(p -> p != null)
                .collect(Collectors.toSet());
    }
}
