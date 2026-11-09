package com.example.e_commerce.mapper;

import com.example.e_commerce.dto.request.CategoryTypeRequest;
import com.example.e_commerce.dto.request.PaymentMethodRequest;
import com.example.e_commerce.dto.response.CategoryTypeResponse;
import com.example.e_commerce.dto.response.PaymentMethodResponse;
import com.example.e_commerce.entity.CategoryType;
import com.example.e_commerce.entity.PaymentMethod;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PaymentMethodMapper {
    PaymentMethodResponse toPaymentMethodResponse(PaymentMethod paymentMethod);

    PaymentMethod toPaymentMethod(PaymentMethodRequest request);

    void updatePaymentMethod(@MappingTarget PaymentMethod paymentMethod , PaymentMethodRequest request);
}
