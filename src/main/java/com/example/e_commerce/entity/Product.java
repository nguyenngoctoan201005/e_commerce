package com.example.e_commerce.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder
@EqualsAndHashCode(of = "productId")
@Table(name = "products", indexes = {
        @Index(name = "idx_product_name", columnList = "name"),
        @Index(name = "idx_product_unit_price", columnList = "unitPrice"),
        @Index(name = "idx_product_category", columnList = "category_id"),
        @Index(name = "idx_product_type", columnList = "type_id")
})
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    Long productId;

    String name;
    BigDecimal unitPrice;
    String unit;
    Double weight;
    String description;


    @OneToMany(mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @org.hibernate.annotations.BatchSize(size = 30)
    @Builder.Default
    Set<ProductImage> productImages = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    CategoryType categoryType;

    public void addProductImage(ProductImage productImage) {
        this.productImages.add(productImage);
        productImage.setProduct(this);
    }

    public void removeProductImage(ProductImage productImage) {
        this.productImages.remove(productImage);
        productImage.setProduct(null);
    }

    public void clearProductImage(){
        for(ProductImage primg : this.getProductImages()){
            primg.setProduct(null);
        }

        this.getProductImages().clear();
    }
}
