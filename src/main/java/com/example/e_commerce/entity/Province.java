package com.example.e_commerce.entity;

import com.example.e_commerce.enums.Region;
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
@Table(name = "provinces")
public class Province {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "province_id")
    Long provinceId;

    String provinceName;

    @Enumerated(EnumType.STRING)
    @Column(name = "region", length = 20)
    Region region;

    @OneToMany(mappedBy = "province")
    Set<District> districts = new HashSet<>();

}
