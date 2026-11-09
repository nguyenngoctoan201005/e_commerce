package com.example.e_commerce.repository;

import com.example.e_commerce.dto.response.review.ReviewResponse;
import com.example.e_commerce.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {


    @Query("""
                SELECT new com.example.e_commerce.dto.response.review.ReviewResponse(
                    r.reviewId,
                    r.ratingStar,
                    r.comment,
                    r.status,
                    r.replyComment,
                    c.customerId,
                    c.fullname,
                    c.email,
                    p.productId,
                    p.name,
                    e.employeeId,
                    e.fullname
                )
                FROM Review r
                JOIN r.customer c
                JOIN r.product p
                LEFT JOIN r.employee e
            """)
    Page<ReviewResponse> getAllReviews(Pageable pageable);

    @Query("""
                SELECT new com.example.e_commerce.dto.response.review.ReviewResponse(
                    r.reviewId,
                    r.ratingStar,
                    r.comment,
                    r.status,
                    r.replyComment,
                    c.customerId,
                    c.fullname,
                    c.email,
                    p.productId,
                    p.name,
                    e.employeeId,
                    e.fullname
                )
                FROM Review r
                JOIN r.customer c
                JOIN r.product p
                LEFT JOIN r.employee e
                WHERE p.productId = :productId
                AND r.status != 'AN_DANH_GIA'
            """)
    Page<ReviewResponse> findReviewsByProductId(Long productId, Pageable pageable);

}