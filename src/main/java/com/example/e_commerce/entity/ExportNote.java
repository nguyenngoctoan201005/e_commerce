package com.example.e_commerce.entity;

import com.example.e_commerce.entity.Employee;
import com.example.e_commerce.entity.GoodsDeliveryDetail;
import com.example.e_commerce.entity.Warehouse;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder
@Table(name = "export_notes")
public class ExportNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    Long deliveryId;

    LocalDateTime export_date;
    String purpose;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    Employee employee;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    Warehouse warehouse;

    @OneToMany(mappedBy = "exportNote",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @Builder.Default
    Set<GoodsDeliveryDetail> goodsDeliveryDetails = new HashSet<>();
}
