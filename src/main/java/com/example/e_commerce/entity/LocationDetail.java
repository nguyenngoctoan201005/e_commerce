package com.example.e_commerce.entity;

import com.example.e_commerce.entity.Location;
import com.example.e_commerce.entity.Product;
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
@Table(name = "location_details", indexes = {
        @Index(name = "idx_locdetail_loc_prod", columnList = "location_id, product_id"),
        @Index(name = "idx_locdetail_product", columnList = "product_id")
})
public class LocationDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="location_detail_id")
    Long locationDetailId;

    Long stockQuantity;

    @ManyToOne
    @JoinColumn(name ="location_id")
    Location location;

    @ManyToOne
    @JoinColumn(name ="product_id")
    Product product;
}
