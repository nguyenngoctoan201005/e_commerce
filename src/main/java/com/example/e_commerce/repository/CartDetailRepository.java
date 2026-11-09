package com.example.e_commerce.repository;

import com.example.e_commerce.entity.Cart;
import com.example.e_commerce.entity.CartDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail, Long> {
    List<CartDetail> findByCart_CartId(Long cartId);
    Optional<CartDetail> findByCart_CartIdAndProduct_ProductId(Long cartId , Long productId );

    @Modifying
    @Query("DELETE FROM CartDetail cd " +
            "WHERE cd.cart.cartId = :cartId")
    void deleteAllByCartId(@Param("cartId") Long cartId);


    interface CartItemProjection {
        Long getProductId();
        Long getQuantity();
    }


    @Query("""
    SELECT cd.product.productId AS productId, cd.quantity AS quantity 
    FROM CartDetail cd 
    WHERE cd.cart.customer.customerId = :customerId 
      AND cd.product.productId IN :productIds
""")
    List<CartItemProjection> findCartItemsByCustomerIdAndProductIds(
            @Param("productIds") List<Long> productIds,
            @Param("customerId") String customerId
    );

    @Query("""
        SELECT cd.product.productId 
        FROM CartDetail cd 
        WHERE cd.cart.customer.customerId = :customerId
    """)
    List<Long> findProductIdsByCustomerId(@Param("customerId") String customerId);

}
