package com.example.e_commerce.dto.response.customer;

import com.example.e_commerce.dto.response.order.MarketingOrderSummaryResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarketingCustomerDetailResponse {
    String customerId;
    String fullname;
    String email;
    String phoneNumber;
    String address;
    String districtName;
    String provinceName;
    Long totalOrders;
    BigDecimal totalSpent;
    List<MarketingOrderSummaryResponse> orders;
}

