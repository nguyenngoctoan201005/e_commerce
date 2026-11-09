package com.example.e_commerce.entity;

import jakarta.persistence.Entity;
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
@Table(name = "carts")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    Long cartId;

    @OneToOne
    @JoinColumn(name = "customer_id")
    Customer customer;

    @OneToMany(mappedBy = "cart",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    Set<CartDetail> cartDetails = new HashSet<>();



    public void addCartDetail(CartDetail detail) {
        this.cartDetails.add(detail);
        detail.setCart(this);
    }

    public void removeCartDetail(CartDetail detail) {
        this.cartDetails.remove(detail);
        detail.setCart(null);
    }

}
