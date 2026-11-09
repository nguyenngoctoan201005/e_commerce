package com.example.e_commerce.repository;

import com.example.e_commerce.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
    Optional<PaymentMethod> findById(Long id);
    boolean existsByPaymentMethodName(String paymentMethodName);
}