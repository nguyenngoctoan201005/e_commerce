package com.example.e_commerce.entity;

import com.example.e_commerce.entity.Employee;
import com.example.e_commerce.entity.GoodsReceiptDetail;
import com.example.e_commerce.entity.Warehouse;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
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
@Table(name = "import_notes")
public class ImportNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "receipt_id")
    Long receiptId;

    LocalDateTime import_date;
    String supplier;

    @OneToMany(mappedBy = "importNote",
            cascade = CascadeType.ALL,
            fetch =FetchType.LAZY)
    @Builder.Default
    Set<GoodsReceiptDetail> goodsReceiptDetails = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "employee_id")
    Employee employee;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    Warehouse warehouse;

}
