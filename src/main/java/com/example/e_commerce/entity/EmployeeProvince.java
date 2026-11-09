package com.example.e_commerce.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder
@Table(name = "employee_provinces")
public class EmployeeProvince {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String description;

    @ManyToOne
    @JoinColumn(name ="employee_id")
    Employee employee;

    @ManyToOne
    @JoinColumn(name ="province_id")
    Province province;

}
