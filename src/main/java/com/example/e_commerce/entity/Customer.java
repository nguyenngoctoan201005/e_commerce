package com.example.e_commerce.entity;

import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "customers")
public class Customer implements AuthUser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "customer_id")
    String customerId;

    @Column(name = "email", unique = true, nullable = false)
    String email;
    String password;
    String fullname;
    String address;
    String phoneNumber;

    @OneToMany(mappedBy = "customer" ,cascade = CascadeType.ALL)
    @Builder.Default
    Set<Review> reviews = new HashSet<>();

    @OneToOne(mappedBy = "customer",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    Cart cart ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id")
    District district;

    @Override
    public String getUsernameOrEmail() {
        return this.getEmail();
    }

    @Override
    public String getUserType() {
        return "CUSTOMER";
    }

    @Override
    public Set<Role> getRoles() {
        return Collections.emptySet();
    }
}
