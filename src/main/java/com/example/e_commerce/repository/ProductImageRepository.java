package com.example.e_commerce.repository;

import com.example.e_commerce.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProduct_ProductId(Long productId);

    @Query("SELECT pi.product.productId AS productId, MIN(pi.imageUrl) AS imageUrl " +
            "FROM ProductImage pi " +
            "WHERE pi.product.productId IN :productIds " +
            "GROUP BY pi.product.productId")
    List<ProductImageProjection> findMainImagesByProductIds(@Param("productIds") List<Long> productIds);

    interface ProductImageProjection {
        Long getProductId();
        String getImageUrl();
    }
}