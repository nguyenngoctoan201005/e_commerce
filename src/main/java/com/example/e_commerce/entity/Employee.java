package com.example.e_commerce.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder
@Table(name = "employees")
public class Employee implements AuthUser {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "employee_id")
    String employeeId;

    String fullname;
    String idCardNumber;
    @Column(name = "username", unique = true, nullable = false)
    String username;
    String password;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    Set<EmployeeProvince> employeeProvinces = new HashSet<>() ;

    @ManyToMany(fetch = FetchType.LAZY)
    @org.hibernate.annotations.BatchSize(size = 30)
    @JoinTable(
            name = "employee_role",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    Set<Role> roles = new HashSet<>();

    @Override
    public String getUsernameOrEmail() {
        return this.username;
    }

    @Override
    public String getUserType() {
        return "EMPLOYEE";
    }

    @Override
    public Set<Role> getRoles() {
        return this.roles;
    }
}
