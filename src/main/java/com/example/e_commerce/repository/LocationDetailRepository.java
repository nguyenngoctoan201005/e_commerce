package com.example.e_commerce.repository;

import com.example.e_commerce.dto.response.location.ProductInLocationResponse;
import com.example.e_commerce.dto.response.warehouse.WarehouseItemResponse;
import com.example.e_commerce.entity.LocationDetail;
import com.example.e_commerce.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationDetailRepository extends JpaRepository<LocationDetail, Long> {

    @Query("SELECT COALESCE(SUM(ld.stockQuantity), 0L) " +
            "FROM LocationDetail ld " +
            "WHERE ld.product.productId = :productId")
    Long getTotalStockByProductId(@Param("productId") Long productId);

    interface ProductStockProjection {
        Long getProductId();

        Long getTotalStock();
    }



    @Query("SELECT ld.product.productId AS productId, COALESCE(SUM(ld.stockQuantity), 0) AS totalStock " +
            "FROM LocationDetail ld " +
            "WHERE ld.product.productId IN :productIds " +
            "GROUP BY ld.product.productId")
    List<ProductStockProjection> findTotalStockByProductIds(@Param("productIds") List<Long> productIds);


    @Query("""
                SELECT COALESCE(SUM(ld.stockQuantity), 0L)
                FROM LocationDetail ld
                JOIN ld.location l
                JOIN l.warehouse w
                WHERE ld.product.productId = :productId
                  AND w.status = 'ACTIVE'
            """)
    Long countTotalStockByProductId(@Param("productId") Long productId);


    @Query("""
                SELECT COALESCE(SUM(ld.stockQuantity), 0L) 
                FROM LocationDetail ld 
                WHERE ld.location.warehouse.warehouseId = :warehouseId 
                  AND ld.product.productId = :productId
            """)
    Long getTotalStockByWarehouseAndProduct(@Param("warehouseId") Long warehouseId,
                                            @Param("productId") Long productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
                SELECT ld FROM LocationDetail ld 
                JOIN ld.location l 
                WHERE l.warehouse.warehouseId = :warehouseId 
                AND ld.product.productId = :productId 
                ORDER BY ld.stockQuantity DESC
            """)
    List<LocationDetail> findByWarehouseAndProductWithLock(
            @Param("warehouseId") Long warehouseId,
            @Param("productId") Long productId
    );

    Optional<LocationDetail> findByLocationLocationIdAndProductProductId(Long locationId, Long productId);

    @Query(value = """
                SELECT new com.example.e_commerce.dto.response.warehouse.WarehouseItemResponse(
                    ld.locationDetailId,
                    l.locationId,
                    l.locationName,
                    p.productId,
                    p.name,
                    ld.stockQuantity
                )
                FROM LocationDetail ld
                JOIN ld.location l
                JOIN ld.product p
                WHERE l.warehouse.warehouseId = :warehouseId
            """,
            countQuery = """
                        SELECT COUNT(ld) 
                        FROM LocationDetail ld 
                        WHERE ld.location.warehouse.warehouseId = :warehouseId
                    """)
    Page<WarehouseItemResponse> getWarehouseItems(
            @Param("warehouseId") Long warehouseId,
            Pageable pageable
    );

    @Query("SELECT COALESCE(SUM(ld.stockQuantity), 0L) " +
            "FROM LocationDetail ld " +
            "WHERE ld.location.locationId = :locationId")
    Long sumStockQuantityByLocationId(@Param("locationId") Long locationId);

    @Modifying
    @Query("DELETE " +
            "FROM LocationDetail ld " +
            "WHERE ld.location.locationId = :locationId")
    void deleteByLocationId(@Param("locationId") Long locationId);



    @Query("""
                SELECT new com.example.e_commerce.dto.response.location.ProductInLocationResponse(
                    ld.locationDetailId,
                    l.locationId,
                    l.locationName,
                    l.description,
                    ld.stockQuantity
                )
                FROM LocationDetail ld
                JOIN ld.location l
                JOIN l.warehouse w
                WHERE w.warehouseId = :warehouseId 
                  AND ld.product.productId = :productId
            """)
    List<ProductInLocationResponse> findLocationsByWarehouseAndProduct(
            @Param("warehouseId") Long warehouseId,
            @Param("productId") Long productId
    );
}