package com.example.e_commerce.entity;
import com.example.e_commerce.enums.ReviewStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name="reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="review_id")
    Long reviewId;

    Integer ratingStar;
    String comment;

    @Enumerated(EnumType.STRING)
    ReviewStatus status;
    String replyComment;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    Employee employee;

    @ManyToOne
    @JoinColumn(name ="product_id")
    Product product;

    @ManyToOne
    @JoinColumn(name ="customer_id")
    Customer customer;
}
