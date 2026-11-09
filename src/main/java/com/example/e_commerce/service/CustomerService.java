package com.example.e_commerce.service;

import com.example.e_commerce.dto.request.customer.ChangePasswordRequest;
import com.example.e_commerce.dto.request.customer.CustomerRegisterRequest;
import com.example.e_commerce.dto.request.customer.CustomerUpdateRequest;
import com.example.e_commerce.dto.response.customer.*;
import com.example.e_commerce.dto.response.order.MarketingOrderDetailResponse;
import com.example.e_commerce.dto.response.order.MarketingOrderSummaryResponse;
import com.example.e_commerce.entity.Cart;
import com.example.e_commerce.entity.Customer;
import com.example.e_commerce.entity.District;
import com.example.e_commerce.entity.Order;
import com.example.e_commerce.enums.ErrorCode;
import com.example.e_commerce.enums.OrderStatus;
import com.example.e_commerce.exception.AppException;
import com.example.e_commerce.mapper.CustomerMapper;
import com.example.e_commerce.mapper.OrderMapper;
import com.example.e_commerce.repository.CustomerRepository;
import com.example.e_commerce.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerService {
    CustomerRepository customerRepository;
    DistrictService districtService;
    CustomerMapper customerMapper;
    PasswordEncoder passwordEncoder;
    OrderRepository orderRepository;
    OrderMapper orderMapper;

    @Transactional
    public CustomerRegisterResponse registerCustomer(CustomerRegisterRequest request){
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        Customer customer = Customer.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .address(request.getAddress())
                .fullname(request.getFullname())
                .phoneNumber(request.getPhoneNumber())
                .district(districtService.getDistrictById(request.getDistrictId()))
                .build();

        Cart cart = new Cart();
        cart.setCustomer(customer);
        customer.setCart(cart);

        return customerMapper.toCustomerRegisterResponse(customerRepository.save(customer));
    }



    @Transactional
    public CustomerUpdateResponse updateInfo(String customerId, CustomerUpdateRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_EXISTED));

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String newEmail = request.getEmail().trim();
            if (!newEmail.equalsIgnoreCase(customer.getEmail())) {
                if (customerRepository.existsByEmail(newEmail)) {
                    throw new AppException(ErrorCode.EMAIL_EXISTED);
                }
            }
            request.setEmail(newEmail);
        }

        customerMapper.updateCustomerFromRequest(request, customer);

        if (request.getDistrictId() != null) {
            District district = districtService.getDistrictById(request.getDistrictId());
            customer.setDistrict(district);
        }

        Customer updatedCustomer = customerRepository.save(customer);

        CustomerUpdateResponse response = customerMapper.toCustomerUpdateResponse(updatedCustomer);

        if (updatedCustomer.getDistrict() != null) {
            response.setDistrictName(updatedCustomer.getDistrict().getDistrictName());
        }
        return response;
    }

    public CustomerDetailResponse getMyInfo(String email) {
        Customer customer = findByEmail(email);
        return getCustomerDetail(customer.getCustomerId());
    }

    @Transactional
    public CustomerUpdateResponse updateMyInfo(String email, CustomerUpdateRequest request) {
        Customer customer = findByEmail(email);
        return updateInfo(customer.getCustomerId(), request);
    }

    @Transactional
    public void changeMyPassword(String email, ChangePasswordRequest request) {
        Customer customer = findByEmail(email);
        changePassword(customer.getCustomerId(), request);
    }


    @Transactional
    public void changePassword(String customerId, ChangePasswordRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_EXISTED));

        if (!passwordEncoder.matches(request.getOldPassword(), customer.getPassword())) {
            throw new AppException(ErrorCode.INCORRECT_PASSWORD);
        }

        if (passwordEncoder.matches(request.getNewPassword(), customer.getPassword())) {
            throw new AppException(ErrorCode.NEW_PASSWORD_SAME_AS_OLD);
        }

        customer.setPassword(passwordEncoder.encode(request.getNewPassword()));

        customerRepository.save(customer);
    }

    public Page<CustomerSummaryResponse> getCustomers(Pageable pageable){
        return customerRepository.getCustomers(pageable);
    }

    public CustomerDetailResponse getCustomerDetail(String customerId){
        return customerRepository.getCustomerDetail(customerId)
                .orElseThrow(()-> new AppException(ErrorCode.CUSTOMER_NOT_EXISTED));
    }

    @Transactional
    public String resetPassword(String customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_EXISTED));

        String rawPassword = generatePassword();

        customer.setPassword(passwordEncoder.encode(rawPassword));

        return rawPassword;
    }

    private String generatePassword() {
        SecureRandom random = new SecureRandom();
        int number = 100000 + random.nextInt(900000);
        return "Reset@" + number;
    }



    public Customer findByEmail(String email){
        return customerRepository.findByEmail(email)
                .orElseThrow(()-> new AppException(ErrorCode.CUSTOMER_NOT_EXISTED));
    }

    public Customer findCustomerById(String id){
        return customerRepository.findById(id)
                .orElseThrow(()-> new AppException(ErrorCode.CUSTOMER_NOT_EXISTED));
    }

    public boolean existsById(String id){
        return customerRepository.existsById(id);
    }

    public MarketingCustomerDetailResponse getCustomerDetailForMarketing(String customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_EXISTED));

        List<Order> orders = orderRepository.findByCustomer_CustomerIdOrderByOrderDateDesc(customerId);
        List<MarketingOrderSummaryResponse> orderResponses = orders.stream()
                .map(orderMapper::toMarketingOrderSummaryResponse)
                .toList();

        BigDecimal totalSpent = orderResponses.stream()
                .filter(o -> o.getStatus() != OrderStatus.DA_HUY)
                .map(MarketingOrderSummaryResponse::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String districtName = customer.getDistrict() != null ? customer.getDistrict().getDistrictName() : null;
        String provinceName = (customer.getDistrict() != null && customer.getDistrict().getProvince() != null)
                ? customer.getDistrict().getProvince().getProvinceName()
                : null;

        return MarketingCustomerDetailResponse.builder()
                .customerId(customer.getCustomerId())
                .fullname(customer.getFullname())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .address(customer.getAddress())
                .districtName(districtName)
                .provinceName(provinceName)
                .totalOrders((long) orders.size())
                .totalSpent(totalSpent)
                .orders(orderResponses)
                .build();
    }

    public Page<MarketingOrderSummaryResponse> getCustomerOrdersForMarketing(String customerId, OrderStatus status, Pageable pageable) {
        if (!customerRepository.existsById(customerId)) {
            throw new AppException(ErrorCode.CUSTOMER_NOT_EXISTED);
        }
        Page<Order> orderPage = orderRepository.findByCustomerIdAndStatus(customerId, status, pageable);
        return orderPage.map(orderMapper::toMarketingOrderSummaryResponse);
    }

    public MarketingOrderDetailResponse getCustomerOrderDetailForMarketing(String customerId, Long orderId) {
        Order order = orderRepository.findByOrderIdAndCustomer_CustomerId(orderId, customerId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));
        return orderMapper.toMarketingOrderDetailResponse(order);
    }

}
