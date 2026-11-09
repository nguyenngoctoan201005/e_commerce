package com.example.e_commerce.entity;

import com.example.e_commerce.entity.LocationDetail;
import com.example.e_commerce.entity.Warehouse;
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
@Table(name ="locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="location_id")
    Long locationId;

    String locationName;
    String description;

    @OneToMany(mappedBy = "location")
    @Builder.Default
    Set<LocationDetail> locationDetails = new HashSet<>();

    @ManyToOne
    @JoinColumn(name ="warehouse_id")
    Warehouse warehouse;


}
