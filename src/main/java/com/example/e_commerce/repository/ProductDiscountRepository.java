package com.example.e_commerce.repository;

import com.example.e_commerce.entity.ProductDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductDiscountRepository extends JpaRepository<ProductDiscount, Long> {

    @Query("""
        SELECT pd FROM ProductDiscount pd
        JOIN FETCH pd.discount d
        WHERE pd.product.productId = :productId
    """)
    List<ProductDiscount> findByProduct_ProductIdWithDiscount(@Param("productId") Long productId);

    @Query("""
        SELECT pd FROM ProductDiscount pd
        JOIN FETCH pd.discount d
        WHERE pd.product.productId IN :productIds
          AND d.startDate <= :now
          AND d.endDate >= :now
          AND (pd.applicableQuantity IS NULL OR pd.applicableQuantity > 0)
    """)
    List<ProductDiscount> findAllActiveDiscountsByProductIds(
            @Param("productIds") List<Long> productIds,
            @Param("now") LocalDateTime now
    );

    @Modifying
    @Query("""
        UPDATE ProductDiscount pd 
        SET pd.applicableQuantity = pd.applicableQuantity - :quantity 
        WHERE pd.product.productId = :productId 
          AND pd.discount.discountId = :discountId 
          AND pd.applicableQuantity >= :quantity
    """)
    int decreaseDiscountQuantity(@Param("productId") Long productId,
                                 @Param("discountId") Long discountId,
                                 @Param("quantity") Long quantity);

    @Query("""
            SELECT pd
            FROM ProductDiscount pd
            WHERE pd.product.productId = :productId
            AND pd.discount.discountId = :discountId
            """)
    ProductDiscount findByProductIdAndDiscountId(@Param("productId") Long prductId ,
                                                 @Param("discountId") Long discountId);
}