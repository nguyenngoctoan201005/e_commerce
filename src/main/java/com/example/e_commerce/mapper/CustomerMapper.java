package com.example.e_commerce.mapper;

import com.example.e_commerce.dto.request.customer.CustomerUpdateRequest;
import com.example.e_commerce.dto.response.customer.CustomerRegisterResponse;
import com.example.e_commerce.dto.response.customer.CustomerUpdateResponse;
import com.example.e_commerce.entity.Customer;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(source = "district.districtId", target = "districtId")
    CustomerRegisterResponse toCustomerRegisterResponse(Customer customer);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "district", ignore = true)
    void updateCustomerFromRequest(CustomerUpdateRequest request, @MappingTarget Customer customer);


    @Mapping(source = "district.districtId", target = "districtId")
    CustomerUpdateResponse toCustomerUpdateResponse(Customer customer);

}
