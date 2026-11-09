package com.example.e_commerce.repository;

import com.example.e_commerce.entity.GoodsDeliveryDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GoodsDeliveryDetailRepository extends JpaRepository<GoodsDeliveryDetail, Long> {
    List<GoodsDeliveryDetail> findByExportNote_DeliveryId(Long deliveryId);
}
