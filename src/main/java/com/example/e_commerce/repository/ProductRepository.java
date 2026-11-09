package com.example.e_commerce.repository;

import com.example.e_commerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> , JpaSpecificationExecutor<Product> {

    @Override
    @EntityGraph(attributePaths = {"category", "categoryType"})
    Page<Product> findAll(Specification<Product> spec, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"category", "categoryType"})
    Optional<Product> findById(Long id);

    public interface ProductPriceProjection {
        Long getProductId();
        BigDecimal getUnitPrice();
    }

    @Query("SELECT p.productId as productId, p.unitPrice as unitPrice " +
            "FROM Product p " +
            "WHERE p.productId IN :productIds")
    List<ProductPriceProjection> findPricesByProductIds(@Param("productIds") List<Long> productIds);

    @EntityGraph(attributePaths = {"category", "categoryType"})
    List<Product> findByProductIdIn(List<Long> productIds);

    boolean existsByCategory_CategoryId(Long categoryId);

    boolean existsByCategoryType_TypeId(Long typeId);

}