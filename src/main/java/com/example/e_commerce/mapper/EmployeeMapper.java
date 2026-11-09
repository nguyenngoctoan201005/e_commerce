package com.example.e_commerce.mapper;

import com.example.e_commerce.dto.request.employee.EmployeeUpdateRequest;
import com.example.e_commerce.dto.response.employee.EmployeeSummaryResponse;
import com.example.e_commerce.entity.Employee;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(target = "employeeProvinces", ignore = true)
    @Mapping(target = "roles", ignore = true)
    void updateEmployeeFromRequest(@MappingTarget Employee employee , EmployeeUpdateRequest request);
}

