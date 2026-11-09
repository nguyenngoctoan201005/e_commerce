package com.example.e_commerce.mapper;

import com.example.e_commerce.dto.request.CategoryRequest;
import com.example.e_commerce.dto.request.PaymentMethodRequest;
import com.example.e_commerce.dto.request.warehouse.WarehouseUpdateRequest;
import com.example.e_commerce.dto.response.warehouse.WarehouseSummaryResponse;
import com.example.e_commerce.entity.Category;
import com.example.e_commerce.entity.PaymentMethod;
import com.example.e_commerce.entity.Warehouse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {

    @Mapping(source = "province.provinceName", target = "provinceName")
    @Mapping(source = "employee.employeeId", target = "employeeId")
    @Mapping(source = "employee.fullname", target = "fullname")
    WarehouseSummaryResponse toWarehouseSummaryResponse(Warehouse warehouse);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "province", ignore = true)
    @Mapping(target = "employee", ignore = true)
    void updateWarehouse(@MappingTarget Warehouse warehouse, WarehouseUpdateRequest request);

}
