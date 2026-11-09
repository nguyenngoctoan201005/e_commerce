package com.example.e_commerce.entity;

import com.example.e_commerce.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
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
@Table(name = "orders", indexes = {
        @Index(name = "idx_order_customer_status", columnList = "customer_id, status"),
        @Index(name = "idx_order_order_date", columnList = "orderDate"),
        @Index(name = "idx_order_status", columnList = "status"),
        @Index(name = "idx_order_district", columnList = "district_id")
})
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    Long orderId;

    LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    OrderStatus status;
    String notes;
    String recipientName;
    String recipientPhone;
    String email;
    String shippingAddress;
    BigDecimal shippingFee;
    String shippingCode;


    @OneToMany(mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @org.hibernate.annotations.BatchSize(size = 30)
    @Builder.Default
    Set<OrderDetail> orderDetails = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id")
    District district;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method_id")
    PaymentMethod paymentMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_id")
    Discount discount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    Warehouse warehouse;


    public void addOrderDetail(OrderDetail detail) {
        this.orderDetails.add(detail);
        detail.setOrder(this);
    }

    public void removeOrderDetail(OrderDetail detail) {
        this.orderDetails.remove(detail);
        detail.setOrder(null);
    }

}
