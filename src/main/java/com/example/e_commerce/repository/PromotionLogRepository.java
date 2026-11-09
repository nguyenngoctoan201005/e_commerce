package com.example.e_commerce.repository;

import com.example.e_commerce.entity.PromotionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromotionLogRepository extends JpaRepository<PromotionLog, Long> {

    List<PromotionLog> findByDiscount_DiscountId(Long discountId);

    Page<PromotionLog> findAllByOrderByModifiedAtDesc(Pageable pageable);

    Page<PromotionLog> findByDiscount_DiscountIdOrderByModifiedAtDesc(Long discountId, Pageable pageable);
}