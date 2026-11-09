package com.example.e_commerce.repository;

import com.example.e_commerce.dto.response.permission.PermissionResponse;
import com.example.e_commerce.dto.response.role.RoleSummaryResponse;
import com.example.e_commerce.entity.Permission;
import com.example.e_commerce.entity.Role;
import com.example.e_commerce.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(UserRole role);

    Set<Role> findByRoleNameIn(Set<UserRole> roleNames);

    boolean existsByRoleName(UserRole role);

    Optional<Role> findById(Long id);

    @Query("""
                SELECT new com.example.e_commerce.dto.response.role.RoleSummaryResponse(
                    r.roleId,
                    CAST(r.roleName AS string),
                    COUNT(DISTINCT p.permissionId),
                    COUNT(DISTINCT e.employeeId)
                )
                FROM Role r
                LEFT JOIN r.permissions p
                LEFT JOIN r.employees e
                GROUP BY r.roleId, r.roleName
            """)
    List<RoleSummaryResponse> getRoleSummaries();


    @Query("""
                SELECT new com.example.e_commerce.dto.response.permission.PermissionResponse(
                            p.permissionId,
                            p.permissionName            
                            )
                FROM Role r
                JOIN r.permissions p 
                WHERE r.roleId = :roleId
            """)
    List<PermissionResponse> getAllPermissionOfRole(@Param("roleId") Long roleId);
}