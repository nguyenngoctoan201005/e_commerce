package com.example.e_commerce.service.impl;

import com.example.e_commerce.dto.request.order.CreateOrderRequest;
import com.example.e_commerce.dto.request.order.UpdateShippingCodeRequest;
import com.example.e_commerce.dto.response.order.MarketingOrderDetailResponse;
import com.example.e_commerce.dto.response.order.MarketingOrderSummaryResponse;
import com.example.e_commerce.dto.response.order.OrderSummaryResponse;
import com.example.e_commerce.dto.response.product.ProductDiscountResponse;
import com.example.e_commerce.dto.response.order.OrderResponse;
import com.example.e_commerce.entity.*;
import com.example.e_commerce.enums.ErrorCode;
import com.example.e_commerce.enums.OrderStatus;
import com.example.e_commerce.exception.AppException;
import com.example.e_commerce.mapper.OrderMapper;
import com.example.e_commerce.repository.EmployeeProvinceRepository;
import com.example.e_commerce.repository.EmployeeRepository;
import com.example.e_commerce.repository.OrderRepository;
import com.example.e_commerce.service.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j

public class OrderServiceImpl implements OrderService {
    private final EmployeeRepository employeeRepository;

    CartService cartService;
    ProductService productService;
    DiscountService discountService;
    DistrictService districtService;
    PaymentMethodService paymentMethodService;
    CustomerService customerService;
    WarehouseService wareHouseService;
    OrderRepository orderRepository;
    LocationService locationService;
    OrderMapper orderMapper;
    EmployeeProvinceRepository employeeProvinceRepository;

    @Transactional
    @Override
    public OrderResponse createOrderFromCart(String customerId, CreateOrderRequest request) {
        List<CartDetail> cartDetails = cartService.getCartDetailsByCustomerId(customerId);
        if (cartDetails.isEmpty()) {
            throw new AppException(ErrorCode.CART_IS_EMPTY);
        }

        Map<Long, Long> productQuantityMap = cartDetails.stream()
                .collect(Collectors.toMap(
                        cd -> cd.getProduct().getProductId(),
                        CartDetail::getQuantity,
                        Long::sum
                ));
        List<Long> productIds = new ArrayList<>(productQuantityMap.keySet());

        Map<Long, Product> productMap = productService.getProductsByIds(productIds);
        Map<Long, BigDecimal> productPriceMap = productService.productPriceMap(productIds);
        Map<Long, ProductDiscountResponse> discountMap = discountService.getDiscountMap(productPriceMap);

        District destinationDistrict = districtService.getDistrictById(request.getDistrictId());

        Warehouse selectedWarehouse = findOptimalWarehouse(destinationDistrict, cartDetails);
        BigDecimal shippingFee = computeFeeByDistance(selectedWarehouse.getProvince(),
                destinationDistrict.getProvince());

        Order order = Order.builder()
                .orderDetails(new HashSet<>())
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.CHO_XAC_NHAN)
                .notes(request.getNotes())
                .recipientName(request.getRecipientName())
                .recipientPhone(request.getRecipientPhone())
                .email(request.getEmail())
                .shippingAddress(request.getShippingAddress())
                .district(destinationDistrict)
                .paymentMethod(paymentMethodService.getPaymentMethodById(request.getPaymentMethodId()))
                .discount(request.getDiscountId() != null ? discountService.findDiscountById(request.getDiscountId())
                        : null)
                .shippingCode("SHIP_" + System.currentTimeMillis())
                .shippingFee(shippingFee)
                .customer(customerService.findCustomerById(customerId))
                .employee(null)
                .warehouse(selectedWarehouse)
                .build();

        for (Long productId : productIds) {
            Long quantityOrder = productQuantityMap.get(productId);
            Product currentProduct = productMap.get(productId);
            ProductDiscountResponse discountInfo = discountMap.get(productId);

            if (discountInfo == null || !Boolean.TRUE.equals(discountInfo.getIsActive())) {
                OrderDetail detail = OrderDetail.builder()
                        .product(currentProduct)
                        .quantity(quantityOrder)
                        .priceAtPurchase(productPriceMap.get(productId))
                        .build();
                order.addOrderDetail(detail);
                continue;
            }

            Long quantityDiscount = discountInfo.getApplicableQuantity();

            if (quantityOrder > quantityDiscount && quantityDiscount > 0) {
                OrderDetail discountedDetail = OrderDetail.builder()
                        .product(currentProduct)
                        .quantity(quantityDiscount)
                        .priceAtPurchase(discountInfo.getDiscountedPrice())
                        .build();

                OrderDetail normalDetail = OrderDetail.builder()
                        .product(currentProduct)
                        .quantity(quantityOrder - quantityDiscount)
                        .priceAtPurchase(productPriceMap.get(productId))
                        .build();

                discountService.deductDiscountQuantity(productId, discountInfo.getDiscountId(), quantityDiscount);
                order.addOrderDetail(discountedDetail);
                order.addOrderDetail(normalDetail);
            } else {
                OrderDetail detail = OrderDetail.builder()
                        .product(currentProduct)
                        .quantity(quantityOrder)
                        .priceAtPurchase(discountInfo.getDiscountedPrice())
                        .build();

                discountService.deductDiscountQuantity(productId, discountInfo.getDiscountId(), quantityOrder);
                order.addOrderDetail(detail);
            }
        }

        Order savedOrder = orderRepository.save(order);
        locationService.deductStock(selectedWarehouse.getWarehouseId(), productQuantityMap);
        cartService.clearCart(customerId);

        return orderMapper.toOrderResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getCustomerOrders(String customerId, OrderStatus status, Pageable pageable) {

        if (!customerService.existsById(customerId)) {
            throw new AppException(ErrorCode.CUSTOMER_NOT_EXISTED);
        }

        Page<Order> orderPage = orderRepository.findByCustomerIdAndStatus(customerId, status, pageable);

        return orderPage.map(orderMapper::toOrderResponse);
    }

    public Warehouse findOptimalWarehouse(District destDistrict, List<CartDetail> cartItems) {
        Province destProvince = destDistrict.getProvince();

        List<Warehouse> qualifiedWarehouses = wareHouseService.findWarehousesWithEnoughStock(cartItems);

        if (qualifiedWarehouses.isEmpty()) {
            throw new AppException(ErrorCode.DONT_HAVE_WAREHOUSE_IS_QUALIFIED);
        }

        return qualifiedWarehouses.stream()
                .filter(w -> w.getProvince().getProvinceId().equals(destProvince.getProvinceId()))
                .findFirst()
                .orElseGet(() -> qualifiedWarehouses.stream()
                        .filter(w -> Objects.equals(w.getProvince().getRegion(), destProvince.getRegion()))
                        .findFirst()
                        .orElse(qualifiedWarehouses.get(0)));
    }

    private BigDecimal computeFeeByDistance(Province originProvince, Province destProvince) {
        if (originProvince == null || destProvince == null) {
            return new BigDecimal("40000");
        }

        if (originProvince.getProvinceId().equals(destProvince.getProvinceId())) {
            return new BigDecimal("15000");
        }

        if (Objects.equals(originProvince.getRegion(), destProvince.getRegion())) {
            return new BigDecimal("25000");
        }

        return new BigDecimal("40000");
    }

    @Override
    public OrderResponse getOrderDetailForCustomer(Long orderId, String customerId) {
        Order order = orderRepository.findByOrderIdAndCustomer_CustomerId(orderId, customerId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));
        return orderMapper.toOrderResponse(order);
    }

    @Transactional
    @Override
    public OrderResponse cancelOrderByCustomer(Long orderId, String customerId) {
        Order order = orderRepository.findByOrderIdAndCustomer_CustomerId(orderId, customerId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        if (order.getStatus() != OrderStatus.CHO_XAC_NHAN) {
            throw new AppException(ErrorCode.ORDER_CAN_NOT_CANCEL);
        }

        Map<Long, Long> productQuantityMap = order.getOrderDetails().stream()
                .collect(Collectors.toMap(
                        detail -> detail.getProduct().getProductId(),
                        OrderDetail::getQuantity,
                        Long::sum));
        Warehouse warehouse = order.getWarehouse();
        if (warehouse == null) {
            throw new AppException(ErrorCode.WAREHOUSE_NOT_EXISTED);
        }

        locationService.restoreStock(order.getWarehouse().getWarehouseId(), productQuantityMap);

        order.setStatus(OrderStatus.DA_HUY);
        order.setNotes("Đơn hàng đã được khách hàng hủy thành công.");

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toOrderResponse(savedOrder);
    }

    @Override
    public List<OrderSummaryResponse> findOrdersByProvinceId(Long provinceId) {
        return orderRepository.findOrdersByProvinceId(provinceId);
    }

    @Transactional
    @Override
    public OrderResponse confirmOrder(String employeeId, Long orderId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        if (order.getStatus() != OrderStatus.CHO_XAC_NHAN) {
            throw new AppException(ErrorCode.INVALID_ORDER_STATUS);
        }

        order.setEmployee(employee);
        order.setStatus(OrderStatus.DA_XAC_NHAN);

        return orderMapper.toOrderResponse(order);
    }

    public BigDecimal getTotalAmount(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        BigDecimal subTotal = order.getOrderDetails().stream()
                .map(detail -> detail.getPriceAtPurchase().multiply(BigDecimal.valueOf(detail.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal shippingFee = order.getShippingFee() != null ? order.getShippingFee() : BigDecimal.ZERO;
        BigDecimal discountAmount = (order.getDiscount() != null && order.getDiscount().getDiscountAmount() != null)
                ? order.getDiscount().getDiscountAmount()
                : BigDecimal.ZERO;

        BigDecimal totalAmount = subTotal.add(shippingFee).subtract(discountAmount);
        return totalAmount.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : totalAmount;
    }

    @Override
    @Transactional
    public OrderSummaryResponse updateStatusOrder(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        if (order.getStatus() == OrderStatus.DA_HUY || order.getStatus() == OrderStatus.THANH_CONG) {
            throw new AppException(ErrorCode.CANNOT_UPDATE_FINALIZED_ORDER);
        }

        if (order.getStatus() != newStatus) {
            order.setStatus(newStatus);
        }
        OrderSummaryResponse response = orderMapper.toOrderSummaryResponse(order);
        response.setTotalAmount(this.getTotalAmount(orderId));
        return response;
    }

    @Override
    public OrderResponse getOrderDetailForEmployee(Long orderId, String employeeId) {
        Order order = orderRepository.findByOrderIdAndEmployee_EmployeeId(orderId, employeeId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));
        return orderMapper.toOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrders(Pageable pageable) {
        Page<Order> orderPage = orderRepository.findAll(pageable);
        return orderPage.map(orderMapper::toOrderResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByEmployeeProvinces(String employeeId, OrderStatus status, Pageable pageable) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED);
        }

        List<EmployeeProvince> employeeProvinces = employeeProvinceRepository.findByEmployee_EmployeeId(employeeId);
        if (employeeProvinces.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        List<Long> provinceIds = employeeProvinces.stream()
                .map(ep -> ep.getProvince().getProvinceId())
                .distinct()
                .toList();

        Page<Order> orderPage = orderRepository.findByProvinceIdsAndStatus(provinceIds, status, pageable);
        return orderPage.map(orderMapper::toOrderResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderDetailByEmployeeProvinces(Long orderId, String employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED);
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        if (order.getDistrict() == null || order.getDistrict().getProvince() == null) {
            throw new AppException(ErrorCode.LOCATION_NOT_EXISTED);
        }

        Long orderProvinceId = order.getDistrict().getProvince().getProvinceId();
        List<Long> managedProvinceIds = employeeProvinceRepository.findByEmployee_EmployeeId(employeeId)
                .stream()
                .map(ep -> ep.getProvince().getProvinceId())
                .toList();

        if (!managedProvinceIds.contains(orderProvinceId)) {
            throw new AppException(ErrorCode.ORDER_NOT_IN_MANAGED_PROVINCES);
        }

        return orderMapper.toOrderResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse updateShippingCode(Long orderId, String employeeId, UpdateShippingCodeRequest request) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED);
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        if (order.getStatus() == OrderStatus.DA_HUY || order.getStatus() == OrderStatus.THANH_CONG) {
            throw new AppException(ErrorCode.CANNOT_UPDATE_FINALIZED_ORDER);
        }

        if (order.getDistrict() == null || order.getDistrict().getProvince() == null) {
            throw new AppException(ErrorCode.LOCATION_NOT_EXISTED);
        }

        Long orderProvinceId = order.getDistrict().getProvince().getProvinceId();
        List<Long> managedProvinceIds = employeeProvinceRepository.findByEmployee_EmployeeId(employeeId)
                .stream()
                .map(ep -> ep.getProvince().getProvinceId())
                .toList();

        if (!managedProvinceIds.contains(orderProvinceId)) {
            throw new AppException(ErrorCode.ORDER_NOT_IN_MANAGED_PROVINCES);
        }

        order.setShippingCode(request.getShippingCode());

        if (Boolean.TRUE.equals(request.getUpdateToShippingStatus()) ||
                (request.getUpdateToShippingStatus() == null && order.getStatus() == OrderStatus.DA_XAC_NHAN)) {
            order.setStatus(OrderStatus.DANG_GIAO);
        }

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toOrderResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MarketingOrderSummaryResponse> getOrdersForMarketing(OrderStatus status, Pageable pageable) {
        Page<Order> orderPage = orderRepository.findAllByStatus(status, pageable);
        return orderPage.map(orderMapper::toMarketingOrderSummaryResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public MarketingOrderDetailResponse getOrderDetailForMarketing(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));
        return orderMapper.toMarketingOrderDetailResponse(order);
    }

}
