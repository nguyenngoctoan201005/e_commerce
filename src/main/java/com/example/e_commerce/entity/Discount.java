package com.example.e_commerce.entity;

import com.example.e_commerce.entity.ProductDiscount;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
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
@Table(name = "discounts")
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "discount_id")
    Long discountId;

    BigDecimal discountAmount;
    LocalDateTime startDate;
    LocalDateTime endDate;
    String description;


    @OneToMany(mappedBy = "discount",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @Builder.Default
    Set<ProductDiscount> productDiscounts = new HashSet<>();

    public void addProductDiscount(ProductDiscount productDiscount){
        this.productDiscounts.add(productDiscount);
        productDiscount.setDiscount(this);
    }

}
