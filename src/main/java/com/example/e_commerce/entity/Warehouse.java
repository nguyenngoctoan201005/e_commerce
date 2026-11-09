package com.example.e_commerce.entity;

import com.example.e_commerce.enums.WarehouseStatus;
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
@EqualsAndHashCode(of = "warehouseId")
@Table(name = "warehouses")
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "warehouse_id")
    Long warehouseId;
    String warehouseName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    WarehouseStatus status;

    @OneToMany(mappedBy = "warehouse")
    @Builder.Default
    Set<Location> locations = new HashSet<>();

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL)
    @Builder.Default
    Set<Order> orders = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "province_id")
    Province province;

    @OneToOne
    @JoinColumn(name = "employee_id")
    Employee employee;
}
