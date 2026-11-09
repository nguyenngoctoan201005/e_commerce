package com.example.e_commerce.mapper;

import com.example.e_commerce.dto.response.review.ReviewResponse;
import com.example.e_commerce.dto.response.warehouse.WarehouseSummaryResponse;
import com.example.e_commerce.entity.Review;
import com.example.e_commerce.entity.Warehouse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(source = "employee.employeeId", target = "employeeId")
    @Mapping(source = "employee.fullname", target = "employeeName")

    @Mapping(source = "customer.customerId", target = "customerId")
    @Mapping(source = "customer.fullname", target = "customerName")
    @Mapping(source = "customer.email", target = "customerEmail")

    @Mapping(source = "product.productId", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    ReviewResponse toReviewResponse(Review review);
}
