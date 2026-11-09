package com.example.e_commerce.mapper;


import com.example.e_commerce.dto.response.location.LocationInWarehouseResponse;
import com.example.e_commerce.entity.Location;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    @Mapping(source = "warehouse.warehouseId", target = "warehouseId")
    @Mapping(source = "warehouse.warehouseName", target = "warehouseName")
    LocationInWarehouseResponse toLocationInWarehouseResponse(Location location);

}
