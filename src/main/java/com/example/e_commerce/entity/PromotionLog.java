package com.example.e_commerce.entity;

import com.example.e_commerce.entity.Discount;
import com.example.e_commerce.entity.Employee;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder
@Table(name="promotion_logs")
public class PromotionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="prom_log_id")
    Long promLogId;

    LocalDateTime modifiedAt;
    String action;

    @ManyToOne
    @JoinColumn(name ="discount_id")
    Discount discount;

    @ManyToOne
    @JoinColumn(name ="emloyee_id")
    Employee employee;

}
