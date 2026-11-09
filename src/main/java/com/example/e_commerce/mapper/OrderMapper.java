package com.example.e_commerce.mapper;

import com.example.e_commerce.dto.response.order.MarketingDiscountResponse;
import com.example.e_commerce.dto.response.order.MarketingOrderDetailResponse;
import com.example.e_commerce.dto.response.order.MarketingOrderSummaryResponse;
import com.example.e_commerce.dto.response.order.OrderItemResponse;
import com.example.e_commerce.dto.response.order.OrderResponse;
import com.example.e_commerce.dto.response.order.OrderSummaryResponse;
import com.example.e_commerce.dto.response.product.ProductResponse;
import com.example.e_commerce.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    default OrderResponse toOrderResponse(Order order) {
        if (order == null) {
            return null;
        }

        BigDecimal subTotal = BigDecimal.ZERO;
        List<OrderItemResponse> itemResponses = new ArrayList<>();

        if (order.getOrderDetails() != null && !order.getOrderDetails().isEmpty()) {
            for (OrderDetail detail : order.getOrderDetails()) {
                Product product = detail.getProduct();

                BigDecimal unitPrice = detail.getPriceAtPurchase() != null ? detail.getPriceAtPurchase()
                        : BigDecimal.ZERO;
                BigDecimal quantityBD = BigDecimal.valueOf(detail.getQuantity() != null ? detail.getQuantity() : 0L);
                BigDecimal totalPrice = unitPrice.multiply(quantityBD);

                subTotal = subTotal.add(totalPrice);

                String imageUrl = null;
                if (product != null && product.getProductImages() != null && !product.getProductImages().isEmpty()) {
                    imageUrl = product.getProductImages().stream()
                            .findFirst()
                            .map(ProductImage::getImageUrl)
                            .orElse(null);
                }

                OrderItemResponse itemDto = OrderItemResponse.builder()
                        .productId(product != null && product.getProductId() != null ? product.getProductId().toString()
                                : null)
                        .productName(product != null ? product.getName() : null)
                        .productImage(imageUrl)
                        .unit(product != null ? product.getUnit() : null)
                        .unitPrice(unitPrice)
                        .quantity(detail.getQuantity())
                        .totalPrice(totalPrice)
                        .build();

                itemResponses.add(itemDto);
            }
        }

        BigDecimal discountAmount = BigDecimal.ZERO;
        String discountCode = null;

        if (order.getDiscount() != null) {
            Discount discount = order.getDiscount();
            discountCode = discount.getDescription();
            discountAmount = discount.getDiscountAmount() != null ? discount.getDiscountAmount() : BigDecimal.ZERO;
        }

        BigDecimal shippingFee = order.getShippingFee() != null ? order.getShippingFee() : BigDecimal.ZERO;
        BigDecimal totalAmount = subTotal.add(shippingFee).subtract(discountAmount);

        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            totalAmount = BigDecimal.ZERO;
        }

        String districtName = order.getDistrict() != null ? order.getDistrict().getDistrictName() : null;
        String provinceName = (order.getDistrict() != null && order.getDistrict().getProvince() != null)
                ? order.getDistrict().getProvince().getProvinceName()
                : null;

        String paymentMethodName = order.getPaymentMethod() != null ? order.getPaymentMethod().getPaymentMethodName()
                : null;
        String employeeName = order.getEmployee() != null ? order.getEmployee().getFullname() : null;

        return OrderResponse.builder()
                .orderId(order.getOrderId() != null ? order.getOrderId().toString() : null)
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .notes(order.getNotes())
                .recipientName(order.getRecipientName())
                .recipientPhone(order.getRecipientPhone())
                .email(order.getEmail())
                .shippingAddress(order.getShippingAddress())
                .districtName(districtName)
                .provinceName(provinceName)
                .shippingCode(order.getShippingCode())
                .paymentMethodName(paymentMethodName)
                .subTotal(subTotal)
                .shippingFee(shippingFee)
                .discountCode(discountCode)
                .discountAmount(discountAmount)
                .totalAmount(totalAmount)
                .employeeName(employeeName)
                .items(itemResponses)
                .build();
    }

    @Mapping(source = "customer.customerId", target = "customerId")
    @Mapping(target = "totalAmount", ignore = true)
    OrderSummaryResponse toOrderSummaryResponse(Order order);

    default MarketingOrderSummaryResponse toMarketingOrderSummaryResponse(Order order) {
        if (order == null) {
            return null;
        }

        BigDecimal subTotal = BigDecimal.ZERO;
        if (order.getOrderDetails() != null) {
            for (OrderDetail detail : order.getOrderDetails()) {
                BigDecimal unitPrice = detail.getPriceAtPurchase() != null ? detail.getPriceAtPurchase() : BigDecimal.ZERO;
                BigDecimal qty = BigDecimal.valueOf(detail.getQuantity() != null ? detail.getQuantity() : 0L);
                subTotal = subTotal.add(unitPrice.multiply(qty));
            }
        }

        BigDecimal shippingFee = order.getShippingFee() != null ? order.getShippingFee() : BigDecimal.ZERO;
        BigDecimal discountAmount = BigDecimal.ZERO;
        String discountCode = null;

        if (order.getDiscount() != null) {
            discountCode = order.getDiscount().getDescription();
            discountAmount = order.getDiscount().getDiscountAmount() != null ? order.getDiscount().getDiscountAmount() : BigDecimal.ZERO;
        }

        BigDecimal totalAmount = subTotal.add(shippingFee).subtract(discountAmount);
        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            totalAmount = BigDecimal.ZERO;
        }

        String customerId = order.getCustomer() != null ? order.getCustomer().getCustomerId() : null;
        String customerName = order.getCustomer() != null ? order.getCustomer().getFullname() : null;

        return MarketingOrderSummaryResponse.builder()
                .orderId(order.getOrderId())
                .customerId(customerId)
                .customerName(customerName)
                .recipientName(order.getRecipientName())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .totalAmount(totalAmount)
                .discountCode(discountCode)
                .discountAmount(discountAmount)
                .build();
    }

    default MarketingOrderDetailResponse toMarketingOrderDetailResponse(Order order) {
        if (order == null) {
            return null;
        }

        BigDecimal subTotal = BigDecimal.ZERO;
        List<OrderItemResponse> itemResponses = new ArrayList<>();

        if (order.getOrderDetails() != null && !order.getOrderDetails().isEmpty()) {
            for (OrderDetail detail : order.getOrderDetails()) {
                Product product = detail.getProduct();
                BigDecimal unitPrice = detail.getPriceAtPurchase() != null ? detail.getPriceAtPurchase() : BigDecimal.ZERO;
                BigDecimal quantityBD = BigDecimal.valueOf(detail.getQuantity() != null ? detail.getQuantity() : 0L);
                BigDecimal totalPrice = unitPrice.multiply(quantityBD);

                subTotal = subTotal.add(totalPrice);

                String imageUrl = null;
                if (product != null && product.getProductImages() != null && !product.getProductImages().isEmpty()) {
                    imageUrl = product.getProductImages().stream()
                            .findFirst()
                            .map(ProductImage::getImageUrl)
                            .orElse(null);
                }

                OrderItemResponse itemDto = OrderItemResponse.builder()
                        .productId(product != null && product.getProductId() != null ? product.getProductId().toString() : null)
                        .productName(product != null ? product.getName() : null)
                        .productImage(imageUrl)
                        .unit(product != null ? product.getUnit() : null)
                        .unitPrice(unitPrice)
                        .quantity(detail.getQuantity())
                        .totalPrice(totalPrice)
                        .build();

                itemResponses.add(itemDto);
            }
        }

        MarketingDiscountResponse discountInfo = null;
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (order.getDiscount() != null) {
            Discount discount = order.getDiscount();
            discountAmount = discount.getDiscountAmount() != null ? discount.getDiscountAmount() : BigDecimal.ZERO;
            discountInfo = MarketingDiscountResponse.builder()
                    .discountId(discount.getDiscountId())
                    .discountCode(discount.getDescription())
                    .discountAmount(discountAmount)
                    .startDate(discount.getStartDate())
                    .endDate(discount.getEndDate())
                    .build();
        }

        BigDecimal shippingFee = order.getShippingFee() != null ? order.getShippingFee() : BigDecimal.ZERO;
        BigDecimal totalAmount = subTotal.add(shippingFee).subtract(discountAmount);
        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            totalAmount = BigDecimal.ZERO;
        }

        String districtName = order.getDistrict() != null ? order.getDistrict().getDistrictName() : null;
        String provinceName = (order.getDistrict() != null && order.getDistrict().getProvince() != null)
                ? order.getDistrict().getProvince().getProvinceName()
                : null;
        String paymentMethodName = order.getPaymentMethod() != null ? order.getPaymentMethod().getPaymentMethodName() : null;
        String customerId = order.getCustomer() != null ? order.getCustomer().getCustomerId() : null;
        String customerName = order.getCustomer() != null ? order.getCustomer().getFullname() : null;

        return MarketingOrderDetailResponse.builder()
                .orderId(order.getOrderId())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .notes(order.getNotes())
                .customerId(customerId)
                .customerName(customerName)
                .recipientName(order.getRecipientName())
                .recipientPhone(order.getRecipientPhone())
                .email(order.getEmail())
                .shippingAddress(order.getShippingAddress())
                .districtName(districtName)
                .provinceName(provinceName)
                .paymentMethodName(paymentMethodName)
                .shippingCode(order.getShippingCode())
                .subTotal(subTotal)
                .shippingFee(shippingFee)
                .discountAmount(discountAmount)
                .totalAmount(totalAmount)
                .discount(discountInfo)
                .items(itemResponses)
                .build();
    }
}