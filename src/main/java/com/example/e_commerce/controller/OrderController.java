package com.example.e_commerce.controller;

import com.example.e_commerce.dto.request.ApiResponse;
import com.example.e_commerce.dto.request.order.CreateOrderRequest;
import com.example.e_commerce.dto.request.order.UpdateShippingCodeRequest;
import com.example.e_commerce.dto.response.order.MarketingOrderDetailResponse;
import com.example.e_commerce.dto.response.order.MarketingOrderSummaryResponse;
import com.example.e_commerce.dto.response.order.OrderResponse;
import com.example.e_commerce.dto.response.order.OrderSummaryResponse;
import com.example.e_commerce.entity.Customer;
import com.example.e_commerce.entity.Employee;
import com.example.e_commerce.enums.ErrorCode;
import com.example.e_commerce.enums.OrderStatus;
import com.example.e_commerce.exception.AppException;
import com.example.e_commerce.repository.EmployeeRepository;
import com.example.e_commerce.service.CustomerService;
import com.example.e_commerce.service.OrderService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {
        OrderService orderService;
        CustomerService customerService;
        private final EmployeeRepository employeeRepository;

        @PostMapping
        @PreAuthorize("hasRole('CUSTOMER')")
        public ApiResponse<OrderResponse> createOrderFromCart(Authentication authentication,
                        @Valid @RequestBody CreateOrderRequest request) {
                String emailCustomer = authentication.getName();
                Customer customer = customerService.findByEmail(emailCustomer);
                String customerId = customer.getCustomerId();
                return ApiResponse.<OrderResponse>builder()
                                .result(orderService.createOrderFromCart(customerId, request))
                                .build();
        }

        @GetMapping("me/{orderId}")
        @PreAuthorize("hasRole('CUSTOMER')")
        public ApiResponse<OrderResponse> getOrderDetailForCustomer(Authentication authentication,
                        @PathVariable Long orderId) {
                String emailCustomer = authentication.getName();
                Customer customer = customerService.findByEmail(emailCustomer);
                String customerId = customer.getCustomerId();

                return ApiResponse.<OrderResponse>builder()
                                .result(orderService.getOrderDetailForCustomer(orderId, customerId))
                                .build();
        }

        @PutMapping("/{orderId}")
        @PreAuthorize("hasRole('CUSTOMER')")
        public ApiResponse<OrderResponse> cancelOrderByCustomer(Authentication authentication,
                        @PathVariable Long orderId) {
                String emailCustomer = authentication.getName();
                Customer customer = customerService.findByEmail(emailCustomer);
                String customerId = customer.getCustomerId();

                return ApiResponse.<OrderResponse>builder()
                                .result(orderService.cancelOrderByCustomer(orderId, customerId))
                                .build();
        }

        @GetMapping("/my-orders")
        @PreAuthorize("hasRole('CUSTOMER')")
        public ApiResponse<Page<OrderResponse>> getMyOrders(
                        Authentication authentication,
                        @RequestParam(required = false) OrderStatus status,
                        @PageableDefault(page = 0, size = 10, sort = "orderDate", direction = Sort.Direction.DESC) Pageable pageable) {
                String emailCustomer = authentication.getName();
                Customer customer = customerService.findByEmail(emailCustomer);
                Page<OrderResponse> result = orderService.getCustomerOrders(customer.getCustomerId(), status, pageable);
                return ApiResponse.<Page<OrderResponse>>builder()
                                .result(result)
                                .build();
        }

        @GetMapping("/customers/{customerId}")
        @PreAuthorize("hasAuthority('ORDER_VIEW_ALL') or hasRole('ADMIN')")
        public ApiResponse<Page<OrderResponse>> getCustomerOrders(
                        @PathVariable String customerId,
                        @RequestParam(required = false) OrderStatus status,
                        @PageableDefault(page = 0, size = 10, sort = "orderDate", direction = Sort.Direction.DESC) Pageable pageable) {
                Page<OrderResponse> result = orderService.getCustomerOrders(customerId, status, pageable);
                return ApiResponse.<Page<OrderResponse>>builder()
                                .result(result)
                                .build();
        }

        @GetMapping("/employee/provinces/{provinceId}")
        @PreAuthorize("hasAuthority('ORDER_VIEW_ALL') or hasRole('SALE') or hasRole('ADMIN')")
        public ApiResponse<List<OrderSummaryResponse>> findOrdersByProvinceId(@PathVariable Long provinceId) {
                List<OrderSummaryResponse> result = orderService.findOrdersByProvinceId(provinceId);
                return ApiResponse.<List<OrderSummaryResponse>>builder()
                                .result(result)
                                .build();
        }

        @PutMapping("/employee/confirm-order/{orderId}")
        @PreAuthorize("hasAuthority('ORDER_APPROVE') or hasRole('SALE') or hasRole('ADMIN')")
        public ApiResponse<OrderResponse> confirmOrder(@PathVariable Long orderId, Authentication authentication) {
                String username = authentication.getName();
                Employee employee = employeeRepository.findByUsername(username)
                                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));
                OrderResponse result = orderService.confirmOrder(employee.getEmployeeId(), orderId);
                return ApiResponse.<OrderResponse>builder()
                                .result(result)
                                .build();
        }

        @PutMapping("/employee/update-status/{orderId}")
        @PreAuthorize("hasAuthority('ORDER_UPDATE') or hasRole('ADMIN')")
        public ApiResponse<OrderSummaryResponse> updateStatusOrder(@PathVariable Long orderId,
                        @RequestBody OrderStatus status) {
                OrderSummaryResponse result = orderService.updateStatusOrder(orderId, status);
                return ApiResponse.<OrderSummaryResponse>builder()
                                .result(result)
                                .build();
        }

        @GetMapping("/employee/order-detail/{orderId}")
        @PreAuthorize("hasAuthority('ORDER_VIEW_ALL') or hasRole('ADMIN')")
        public ApiResponse<OrderResponse> getOrderDetailForEmployee(@PathVariable Long orderId,
                        Authentication authentication) {
                String username = authentication.getName();
                Employee employee = employeeRepository.findByUsername(username)
                                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));
                OrderResponse result = orderService.getOrderDetailForEmployee(orderId, employee.getEmployeeId());
                return ApiResponse.<OrderResponse>builder()
                                .result(result)
                                .build();
        }

        @GetMapping
        @PreAuthorize("hasAuthority('ORDER_VIEW_ALL') or hasRole('ADMIN')")
        public ApiResponse<Page<OrderResponse>> getOrders(
                        @PageableDefault(page = 0, size = 10, sort = "orderId", direction = Sort.Direction.DESC) Pageable pageable) {
                return ApiResponse.<Page<OrderResponse>>builder()
                                .result(orderService.getOrders(pageable))
                                .build();
        }

        @GetMapping("/warehouse/managed-orders")
        @PreAuthorize("hasAuthority('INVENTORY_VIEW') or hasAuthority('WAREHOUSE_VIEW') or hasAuthority('ORDER_VIEW_ALL') or hasRole('WAREHOUSE') or hasRole('ADMIN')")
        public ApiResponse<Page<OrderResponse>> getManagedOrders(
                        @RequestParam(required = false) OrderStatus status,
                        @PageableDefault(size = 10, sort = "orderDate", direction = Sort.Direction.DESC) Pageable pageable,
                        Authentication authentication) {
                String username = authentication.getName();
                Employee employee = employeeRepository.findByUsername(username)
                                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));

                Page<OrderResponse> result = orderService.getOrdersByEmployeeProvinces(
                                employee.getEmployeeId(), status, pageable);

                return ApiResponse.<Page<OrderResponse>>builder()
                                .result(result)
                                .build();
        }

        @GetMapping("/warehouse/managed-orders/{orderId}")
        @PreAuthorize("hasAuthority('INVENTORY_VIEW') or hasAuthority('WAREHOUSE_VIEW') or hasAuthority('ORDER_VIEW_ALL') or hasRole('WAREHOUSE') or hasRole('ADMIN')")
        public ApiResponse<OrderResponse> getManagedOrderDetail(
                        @PathVariable Long orderId,
                        Authentication authentication) {
                String username = authentication.getName();
                Employee employee = employeeRepository.findByUsername(username)
                                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));

                OrderResponse result = orderService.getOrderDetailByEmployeeProvinces(
                                orderId, employee.getEmployeeId());

                return ApiResponse.<OrderResponse>builder()
                                .result(result)
                                .build();
        }

        @PutMapping("/warehouse/managed-orders/{orderId}/shipping-code")
        @PreAuthorize("hasAuthority('INVENTORY_UPDATE') or hasAuthority('WAREHOUSE_UPDATE') or hasAuthority('ORDER_UPDATE') or hasRole('WAREHOUSE') or hasRole('ADMIN')")
        public ApiResponse<OrderResponse> updateShippingCode(
                        @PathVariable Long orderId,
                        @Valid @RequestBody UpdateShippingCodeRequest request,
                        Authentication authentication) {
                String username = authentication.getName();
                Employee employee = employeeRepository.findByUsername(username)
                                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));

                OrderResponse result = orderService.updateShippingCode(
                                orderId, employee.getEmployeeId(), request);

                return ApiResponse.<OrderResponse>builder()
                                .result(result)
                                .message("Cập nhật mã vận chuyển thành công")
                                .build();
        }

        @GetMapping("/marketing")
        @PreAuthorize("hasAuthority('REPORT_MARKETING_VIEW') or hasAuthority('PROMOTION_VIEW') or hasAuthority('ORDER_VIEW_ALL') or hasRole('MARKETING') or hasRole('ADMIN')")
        public ApiResponse<Page<MarketingOrderSummaryResponse>> getOrdersForMarketing(
                        @RequestParam(required = false) OrderStatus status,
                        @PageableDefault(size = 10, sort = "orderDate", direction = Sort.Direction.DESC) Pageable pageable) {
                Page<MarketingOrderSummaryResponse> result = orderService.getOrdersForMarketing(status, pageable);
                return ApiResponse.<Page<MarketingOrderSummaryResponse>>builder()
                                .result(result)
                                .build();
        }

        @GetMapping("/marketing/{orderId}")
        @PreAuthorize("hasAuthority('REPORT_MARKETING_VIEW') or hasAuthority('PROMOTION_VIEW') or hasAuthority('ORDER_VIEW_ALL') or hasRole('MARKETING') or hasRole('ADMIN')")
        public ApiResponse<MarketingOrderDetailResponse> getOrderDetailForMarketing(@PathVariable Long orderId) {
                MarketingOrderDetailResponse result = orderService.getOrderDetailForMarketing(orderId);
                return ApiResponse.<MarketingOrderDetailResponse>builder()
                                .result(result)
                                .build();
        }

}
