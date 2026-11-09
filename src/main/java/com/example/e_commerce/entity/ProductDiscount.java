package com.example.e_commerce.entity;

import com.example.e_commerce.entity.Discount;
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
@Table(name = "product_discounts")
public class ProductDiscount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="product_discount_id")
    Long productDiscountId;

    Long applicableQuantity;

    @ManyToOne
    @JoinColumn(name ="product_id")
    Product product;

    @ManyToOne
    @JoinColumn(name ="discount_id")
    Discount discount;


}
