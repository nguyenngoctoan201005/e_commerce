package com.example.e_commerce.repository;

import com.example.e_commerce.entity.Permission;
import com.example.e_commerce.enums.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByPermissionName(UserPermission name);

    boolean existsByPermissionName(UserPermission userPermission);

    Set<Permission> findByPermissionNameIn(Set<UserPermission> userPermissionSet);


}