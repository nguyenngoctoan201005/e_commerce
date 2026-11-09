package com.example.e_commerce.entity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "edit_logs")
public class EditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="edit_log_id")
    Long editLogId;

    LocalDateTime modifiedAt;
    String description;

    @ManyToOne
    @JoinColumn(name ="product_id")
    Product product;

    @ManyToOne
    @JoinColumn(name ="employee_id")
    Employee employee;

}
