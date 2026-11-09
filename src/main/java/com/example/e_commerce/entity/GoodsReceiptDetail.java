package com.example.e_commerce.entity;

import com.example.e_commerce.entity.ImportNote;
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
@Table(name = "goods_receipt_details")
public class GoodsReceiptDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "receipt_detail_id")
    Long receiptDetailId;

    Integer import_quantity;
    BigDecimal import_price;

    @ManyToOne
    @JoinColumn(name ="receipt_id")
    ImportNote importNote;

    @ManyToOne
    @JoinColumn(name ="location_id")
    Location location;

    @ManyToOne
    @JoinColumn(name ="product_id")
    Product product;
}
