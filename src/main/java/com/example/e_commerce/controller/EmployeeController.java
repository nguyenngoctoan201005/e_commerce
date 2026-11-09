package com.example.e_commerce.controller;

import com.example.e_commerce.dto.request.ApiResponse;
import com.example.e_commerce.dto.request.employee.EmployeeCreateRequest;
import com.example.e_commerce.dto.request.employee.EmployeeUpdateRequest;
import com.example.e_commerce.dto.response.employee.EmployeeDetailResponse;
import com.example.e_commerce.dto.response.employee.EmployeeSummaryResponse;
import com.example.e_commerce.dto.response.role.RoleSummaryResponse;
import com.example.e_commerce.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeController {
    EmployeeService employeeService;

    @GetMapping
    @PreAuthorize("hasAuthority('EMPLOYEE_VIEW') or hasRole('ADMIN')")
    public ApiResponse<Page<EmployeeSummaryResponse>> getEmployeeSummaries(
            @PageableDefault(page = 0, size = 10
            ) Pageable pageable
    ){
        return ApiResponse.<Page<EmployeeSummaryResponse>>builder()
                .result(employeeService.getEmployeeSummaries(pageable))
                .build();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('EMPLOYEE_CREATE') or hasRole('ADMIN')")
    public ApiResponse<EmployeeDetailResponse> createEmployee(@Valid @RequestBody EmployeeCreateRequest request){
        return ApiResponse.<EmployeeDetailResponse>builder()
                .result(employeeService.createEmployee(request))
                .build();
    }

    @GetMapping("/{employeeId}")
    @PreAuthorize("hasAuthority('EMPLOYEE_VIEW') or hasRole('ADMIN')")
    public ApiResponse<EmployeeDetailResponse> getEmployeeDetail(@PathVariable String employeeId){
        return ApiResponse.<EmployeeDetailResponse>builder()
                .result(employeeService.getEmployeeDetail(employeeId))
                .build();
    }

    @PutMapping("/{employeeId}")
    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE') or hasRole('ADMIN')")
    public ApiResponse<EmployeeDetailResponse> updateEmployee(@PathVariable String employeeId,
                                                              @Valid @RequestBody EmployeeUpdateRequest request){
        return ApiResponse.<EmployeeDetailResponse>builder()
                .result(employeeService.updateEmployee(employeeId,request))
                .build();
    }


}
