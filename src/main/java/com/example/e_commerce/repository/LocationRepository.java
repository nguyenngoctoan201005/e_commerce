package com.example.e_commerce.repository;

import com.example.e_commerce.dto.response.location.LocationInWarehouseResponse;
import com.example.e_commerce.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findByWarehouse_WarehouseId(int warehouseId);

    Optional<Location> findByWarehouseWarehouseIdAndLocationName(Long warehouseId, String locationName);

    boolean existsByWarehouseWarehouseIdAndLocationName(Long warehouseId, String locationName);


    @Query("""
            SELECT new com.example.e_commerce.dto.response.location.LocationInWarehouseResponse(
                locationId,
                locationName,
                description,
                w.warehouseId,
                w.warehouseName, 
                COALESCE(SUM(ld.stockQuantity), 0L)  
            )
            FROM Location l
            JOIN l.warehouse w
            LEFT JOIN l.locationDetails ld
            WHERE w.warehouseId = :warehouseId
            GROUP BY
                        l.locationId,
                        l.locationName,
                        l.description,
                        w.warehouseId,
                        w.warehouseName
            
            """)
    List<LocationInWarehouseResponse> getLocationByWarehouseId(@Param("warehouseId") Long warehouseId);


}