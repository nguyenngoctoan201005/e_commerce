package com.example.e_commerce.repository;

import com.example.e_commerce.dto.response.order.OrderSummaryResponse;
import com.example.e_commerce.entity.Order;
import com.example.e_commerce.entity.OrderDetail;
import com.example.e_commerce.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"district", "district.province", "paymentMethod", "discount", "employee"})
    Optional<Order> findByOrderIdAndCustomer_CustomerId(Long orderId, String customerId);

    @EntityGraph(attributePaths = {"district", "district.province", "paymentMethod", "discount", "employee"})
    List<Order> findByCustomer_CustomerIdOrderByOrderDateDesc(String customerId);

    @EntityGraph(attributePaths = {"district", "district.province", "paymentMethod", "discount", "employee"})
    Optional<Order> findByOrderIdAndEmployee_EmployeeId(Long orderId, String employeeId);

    boolean existsByPaymentMethod_PaymentMethodId(Long paymentMethodId);

    boolean existsByDiscount_DiscountId(Long discountId);

    @Override
    @EntityGraph(attributePaths = {"district", "district.province", "paymentMethod", "discount", "employee"})
    Optional<Order> findById(Long orderId);

    @Override
    @EntityGraph(attributePaths = {"district", "district.province", "paymentMethod", "discount", "employee"})
    Page<Order> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"district", "district.province", "paymentMethod", "discount", "employee"})
    @Query("""
                SELECT o FROM Order o 
                WHERE o.customer.customerId = :customerId 
                  AND (cast(:status as string) IS NULL OR o.status = :status)
            """)
    Page<Order> findByCustomerIdAndStatus(
            @Param("customerId") String customerId,
            @Param("status") OrderStatus status,
            Pageable pageable
    );

    @Query("""
                SELECT new com.example.e_commerce.dto.response.order.OrderSummaryResponse(
                    o.orderId,
                    o.customer.customerId,
                    o.recipientName,
                    o.orderDate,
                    o.status,
                    CAST(COALESCE(SUM(od.quantity * od.priceAtPurchase), 0) + COALESCE(o.shippingFee, 0) AS java.math.BigDecimal)
                )
                FROM Order o
                JOIN o.district d
                JOIN d.province p
                LEFT JOIN o.orderDetails od
                WHERE p.provinceId = :provinceId 
                  AND o.status = com.example.e_commerce.enums.OrderStatus.CHO_XAC_NHAN
                GROUP BY o.orderId, o.customer.customerId, o.recipientName, o.orderDate, o.status, o.shippingFee
            """)
    List<OrderSummaryResponse> findOrdersByProvinceId(@Param("provinceId") Long provinceId);

    @EntityGraph(attributePaths = {"district", "district.province", "paymentMethod", "discount", "employee"})
    @Query("""
        SELECT o FROM Order o 
        JOIN o.district d 
        JOIN d.province p 
        WHERE p.provinceId IN :provinceIds 
          AND (cast(:status as string) IS NULL OR o.status = :status)
    """)
    Page<Order> findByProvinceIdsAndStatus(
            @Param("provinceIds") Collection<Long> provinceIds,
            @Param("status") OrderStatus status,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"district", "district.province", "paymentMethod", "discount", "employee"})
    @Query("""
        SELECT o FROM Order o 
        WHERE (cast(:status as string) IS NULL OR o.status = :status)
    """)
    Page<Order> findAllByStatus(@Param("status") OrderStatus status, Pageable pageable);
}