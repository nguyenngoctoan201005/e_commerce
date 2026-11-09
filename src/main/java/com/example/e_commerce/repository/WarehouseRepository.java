package com.example.e_commerce.repository;

import com.example.e_commerce.dto.response.location.ProductInLocationResponse;
import com.example.e_commerce.dto.response.warehouse.ProductDetailInWarehouseResponse;
import com.example.e_commerce.dto.response.warehouse.ProductInWarehouseResponse;
import com.example.e_commerce.dto.response.warehouse.WarehouseSummaryResponse;
import com.example.e_commerce.entity.CartDetail;
import com.example.e_commerce.entity.Warehouse;
import com.example.e_commerce.enums.WarehouseStatus;
import org.aspectj.apache.bcel.classfile.Module;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    List<Warehouse> findAllByStatus(WarehouseStatus status);

    boolean existsById(Long warehouseId);

    Optional<Warehouse> findByEmployee_EmployeeId(String employeeId);


    @Query("""
                SELECT w FROM Warehouse w 
                JOIN Location l ON l.warehouse.warehouseId = w.warehouseId 
                JOIN LocationDetail ld ON ld.location.locationId = l.locationId 
                WHERE w.status = 'ACTIVE' 
                AND ld.product.productId = :productId 
                GROUP BY w 
                HAVING SUM(ld.stockQuantity) >= :requiredQuantity
            """)
    List<Warehouse> findWarehousesWithStockForProduct(
            @Param("productId") Long productId,
            @Param("requiredQuantity") Long requiredQuantity
    );


    @Query(value = """
                SELECT new com.example.e_commerce.dto.response.warehouse.WarehouseSummaryResponse(
                    w.warehouseId,
                    w.warehouseName,
                    w.status,
                    p.provinceName,
                    m.employeeId,
                    m.fullname,
                    COUNT(DISTINCT l.locationId),
                    COALESCE(SUM(ld.stockQuantity), 0L)
                )
                FROM Warehouse w
                LEFT JOIN w.province p
                LEFT JOIN w.employee m          
                LEFT JOIN w.locations l        
                LEFT JOIN l.locationDetails ld 
                GROUP BY 
                    w.warehouseId, 
                    w.warehouseName, 
                    w.status, 
                    p.provinceName, 
                    m.employeeId, 
                    m.fullname
            """,
            countQuery = "SELECT COUNT(w) FROM Warehouse w")
    Page<WarehouseSummaryResponse> getWarehousesSummary(Pageable pageable);


    @Query(value = """
                SELECT new com.example.e_commerce.dto.response.warehouse.WarehouseSummaryResponse(
                    w.warehouseId,
                    w.warehouseName,
                    w.status,
                    p.provinceName,
                    m.employeeId,
                    m.fullname,
                    COUNT(DISTINCT l.locationId),
                    COALESCE(SUM(ld.stockQuantity), 0L)
                )
                FROM Warehouse w
                LEFT JOIN w.province p
                LEFT JOIN w.employee m
                LEFT JOIN w.locations l
                LEFT JOIN l.locationDetails ld
                WHERE w.warehouseId = :warehouseId
                GROUP BY w.warehouseId, w.warehouseName, w.status, p.provinceName, m.employeeId, m.fullname
            """)
    Optional<WarehouseSummaryResponse> getWarehouseSummaryById(@Param("warehouseId") Long warehouseId);


    @Query("""
                SELECT new com.example.e_commerce.dto.response.warehouse.ProductInWarehouseResponse(
                    w.warehouseId,
                    p.productId,
                    p.name,
                    COUNT(DISTINCT l.locationId), 
                    COALESCE(SUM(ld.stockQuantity), 0L)
                )
                FROM Warehouse w
                LEFT JOIN w.locations l
                LEFT JOIN l.locationDetails ld
                LEFT JOIN ld.product p
                WHERE w.warehouseId = :warehouseId 
                  AND w.employee.employeeId = :employeeId 
                  AND p.productId IS NOT NULL
                GROUP BY w.warehouseId, p.productId, p.name
            """)
    List<ProductInWarehouseResponse> getProductsInWarehouse(
            @Param("warehouseId") Long warehouseId,
            @Param("employeeId") String employeeId
    );


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
                WHERE ld.product.productId = :productId
            """)
    List<ProductInLocationResponse> findLocationsByProductId(@Param("productId") Long productId);


}