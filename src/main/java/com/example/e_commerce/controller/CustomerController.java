package com.example.e_commerce.controller;

import com.example.e_commerce.dto.request.ApiResponse;
import com.example.e_commerce.dto.request.customer.ChangePasswordRequest;
import com.example.e_commerce.dto.request.customer.CustomerRegisterRequest;
import com.example.e_commerce.dto.request.customer.CustomerUpdateRequest;
import com.example.e_commerce.dto.response.customer.*;
import com.example.e_commerce.dto.response.order.MarketingOrderDetailResponse;
import com.example.e_commerce.dto.response.order.MarketingOrderSummaryResponse;
import com.example.e_commerce.enums.OrderStatus;
import com.example.e_commerce.service.CustomerService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerController {
    CustomerService customerService;

    @PostMapping
    public ApiResponse<CustomerRegisterResponse> registerCustomer(@Valid @RequestBody CustomerRegisterRequest request){
        CustomerRegisterResponse response = customerService.registerCustomer(request);
        return ApiResponse.<CustomerRegisterResponse>builder()
                .result(response)
                .build();
    }

    @GetMapping("/me")
    public ApiResponse<CustomerDetailResponse> getMyProfile(Authentication authentication) {
        return ApiResponse.<CustomerDetailResponse>builder()
                .result(customerService.getMyInfo(authentication.getName()))
                .build();
    }

    @PutMapping("/me")
    public ApiResponse<CustomerUpdateResponse> updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody CustomerUpdateRequest request
    ) {
        return ApiResponse.<CustomerUpdateResponse>builder()
                .result(customerService.updateMyInfo(authentication.getName(), request))
                .build();
    }

    @PutMapping("/me/password")
    public ApiResponse<Void> changeMyPassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        customerService.changeMyPassword(authentication.getName(), request);
        return ApiResponse.<Void>builder()
                .message("Đổi mật khẩu thành công")
                .build();
    }

    @PutMapping("/{customerId}")
    @PreAuthorize("hasAuthority('CUSTOMER_UPDATE') or hasRole('ADMIN')")
    public ApiResponse<CustomerUpdateResponse> updateCustomer(@PathVariable String customerId, @Valid @RequestBody CustomerUpdateRequest request){
        CustomerUpdateResponse response = customerService.updateInfo(customerId, request);
        return ApiResponse.<CustomerUpdateResponse>builder()
                .result(response)
                .build();
    }

    @PutMapping("/password/{customerId}")
    @PreAuthorize("hasAuthority('CUSTOMER_UPDATE') or hasRole('ADMIN')")
    public ApiResponse<Void> changePassword(@PathVariable String customerId, @Valid @RequestBody ChangePasswordRequest request){
        customerService.changePassword(customerId, request);
        return ApiResponse.<Void>builder()
                .message("Đổi mật khẩu thành công")
                .build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('CUSTOMER_VIEW') or hasAuthority('REPORT_MARKETING_VIEW') or hasRole('MARKETING') or hasRole('ADMIN')")
    public ApiResponse<Page<CustomerSummaryResponse>> getCustomers(
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ){
        Page<CustomerSummaryResponse> response = customerService.getCustomers(pageable);
        return ApiResponse.<Page<CustomerSummaryResponse>>builder()
                .result(response)
                .build();
    }

    @GetMapping("/{customerId}")
    @PreAuthorize("hasAuthority('CUSTOMER_VIEW') or hasAuthority('REPORT_MARKETING_VIEW') or hasRole('MARKETING') or hasRole('ADMIN')")
    public ApiResponse<CustomerDetailResponse> getCustomerDetail(@PathVariable String customerId){
        CustomerDetailResponse response = customerService.getCustomerDetail(customerId);
        return ApiResponse.<CustomerDetailResponse>builder()
                .result(response)
                .build();
    }

    @GetMapping("/{customerId}/detail")
    @PreAuthorize("hasAuthority('CUSTOMER_VIEW') or hasAuthority('REPORT_MARKETING_VIEW') or hasRole('MARKETING') or hasRole('ADMIN')")
    public ApiResponse<MarketingCustomerDetailResponse> getCustomerDetailForMarketing(@PathVariable String customerId) {
        MarketingCustomerDetailResponse response = customerService.getCustomerDetailForMarketing(customerId);
        return ApiResponse.<MarketingCustomerDetailResponse>builder()
                .result(response)
                .build();
    }

    @GetMapping("/{customerId}/orders")
    @PreAuthorize("hasAuthority('CUSTOMER_VIEW') or hasAuthority('REPORT_MARKETING_VIEW') or hasRole('MARKETING') or hasRole('ADMIN')")
    public ApiResponse<Page<MarketingOrderSummaryResponse>> getCustomerOrders(
            @PathVariable String customerId,
            @RequestParam(required = false) OrderStatus status,
            @PageableDefault(size = 10, sort = "orderDate", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Page<MarketingOrderSummaryResponse> response = customerService.getCustomerOrdersForMarketing(customerId, status, pageable);
        return ApiResponse.<Page<MarketingOrderSummaryResponse>>builder()
                .result(response)
                .build();
    }

    @GetMapping("/{customerId}/orders/{orderId}")
    @PreAuthorize("hasAuthority('CUSTOMER_VIEW') or hasAuthority('REPORT_MARKETING_VIEW') or hasRole('MARKETING') or hasRole('ADMIN')")
    public ApiResponse<MarketingOrderDetailResponse> getCustomerOrderDetail(
            @PathVariable String customerId,
            @PathVariable Long orderId) {
        MarketingOrderDetailResponse response = customerService.getCustomerOrderDetailForMarketing(customerId, orderId);
        return ApiResponse.<MarketingOrderDetailResponse>builder()
                .result(response)
                .build();
    }

    @PutMapping("/reset/{customerId}")
    @PreAuthorize("hasAuthority('CUSTOMER_UPDATE') or hasRole('ADMIN')")
    public ApiResponse<String> resetPassword(@PathVariable String customerId){
        return ApiResponse.<String>builder()
                .result(customerService.resetPassword(customerId))
                .build();
    }
}
