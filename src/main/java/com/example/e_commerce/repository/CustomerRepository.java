package com.example.e_commerce.repository;

import com.example.e_commerce.dto.response.customer.CustomerDetailResponse;
import com.example.e_commerce.dto.response.customer.CustomerSummaryResponse;
import com.example.e_commerce.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findById(String customerId);

    boolean existsByEmail(String email);

    boolean existsById(String customerId);

    @Query("""
                SELECT new com.example.e_commerce.dto.response.customer.CustomerSummaryResponse(
                    c.customerId,
                    c.fullname,
                    c.email,
                    COUNT(DISTINCT o.orderId),
                    COALESCE(SUM(od.quantity * od.priceAtPurchase), 0)
                )
                FROM Customer c
                LEFT JOIN Order o ON o.customer = c
                LEFT JOIN o.orderDetails od
                GROUP BY c.customerId, c.fullname, c.email
                ORDER BY COALESCE(SUM(od.quantity * od.priceAtPurchase), 0) DESC
            """)
    Page<CustomerSummaryResponse> getCustomers(Pageable pageable);


    @Query("""
                SELECT new com.example.e_commerce.dto.response.customer.CustomerDetailResponse(
                    c.customerId,
                    c.email,
                    c.phoneNumber,
                    c.fullname,
                    c.address,
                    COUNT(DISTINCT o.orderId),
                    COALESCE(SUM(od.quantity * od.priceAtPurchase), 0)
                )
                FROM Customer c
                LEFT JOIN Order o ON o.customer = c
                LEFT JOIN o.orderDetails od
                WHERE c.customerId = :customerId
                GROUP BY c.customerId, c.email, c.phoneNumber, c.fullname, c.address
            """)
    Optional<CustomerDetailResponse> getCustomerDetail(@Param("customerId") String customerId);
}