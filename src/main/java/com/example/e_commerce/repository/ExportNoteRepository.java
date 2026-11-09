package com.example.e_commerce.repository;

import com.example.e_commerce.entity.ExportNote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExportNoteRepository extends JpaRepository<ExportNote, Long> {

    @Query("SELECT e FROM ExportNote e WHERE e.warehouse.warehouseId = :warehouseId ORDER BY e.export_date DESC")
    List<ExportNote> findByWarehouseId(@Param("warehouseId") Long warehouseId);

    @Query("SELECT e FROM ExportNote e WHERE e.warehouse.warehouseId = :warehouseId ORDER BY e.export_date DESC")
    Page<ExportNote> findByWarehouseId(@Param("warehouseId") Long warehouseId, Pageable pageable);

    @Query("SELECT e FROM ExportNote e WHERE e.employee.employeeId = :employeeId ORDER BY e.export_date DESC")
    List<ExportNote> findByEmployeeId(@Param("employeeId") String employeeId);

    @Query("SELECT e FROM ExportNote e WHERE e.employee.employeeId = :employeeId ORDER BY e.export_date DESC")
    Page<ExportNote> findByEmployeeId(@Param("employeeId") String employeeId, Pageable pageable);
}
