package com.example.e_commerce.entity;

import com.example.e_commerce.entity.ExportNote;
import com.example.e_commerce.entity.Location;
import com.example.e_commerce.entity.Product;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder
@Table(name = "goods_delivery_details")
public class GoodsDeliveryDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="delivery_detail_id")
    Long deliveryDetailId;

    Integer export_quantity;
    BigDecimal export_price;

    @ManyToOne
    @JoinColumn(name ="product_id")
    Product product;

    @ManyToOne
    @JoinColumn(name ="location_id")
    Location location;

    @ManyToOne
    @JoinColumn(name ="deliver_id")
    ExportNote exportNote;
}
