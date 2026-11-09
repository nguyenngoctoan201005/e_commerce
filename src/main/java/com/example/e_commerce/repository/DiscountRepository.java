package com.example.e_commerce.repository;

import com.example.e_commerce.entity.Discount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    Optional<Discount> findById(Long id);

    @Query("SELECT d FROM Discount d WHERE :now BETWEEN d.startDate AND d.endDate")
    Page<Discount> findActiveDiscounts(@Param("now") LocalDateTime now, Pageable pageable);
}