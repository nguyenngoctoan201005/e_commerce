package com.example.e_commerce.repository;

import com.example.e_commerce.entity.ImportNote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImportNoteRepository extends JpaRepository<ImportNote, Long> {

    @Query("SELECT i FROM ImportNote i WHERE i.warehouse.warehouseId = :warehouseId ORDER BY i.import_date DESC")
    List<ImportNote> findByWarehouseId(@Param("warehouseId") Long warehouseId);

    @Query("SELECT i FROM ImportNote i WHERE i.warehouse.warehouseId = :warehouseId ORDER BY i.import_date DESC")
    Page<ImportNote> findByWarehouseId(@Param("warehouseId") Long warehouseId, Pageable pageable);

    @Query("SELECT i FROM ImportNote i WHERE i.employee.employeeId = :employeeId ORDER BY i.import_date DESC")
    List<ImportNote> findByEmployeeId(@Param("employeeId") String employeeId);

    @Query("SELECT i FROM ImportNote i WHERE i.employee.employeeId = :employeeId ORDER BY i.import_date DESC")
    Page<ImportNote> findByEmployeeId(@Param("employeeId") String employeeId, Pageable pageable);
}
