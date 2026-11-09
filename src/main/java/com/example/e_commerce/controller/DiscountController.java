package com.example.e_commerce.controller;

import com.example.e_commerce.dto.request.ApiResponse;
import com.example.e_commerce.dto.request.discount.DiscountRequest;
import com.example.e_commerce.dto.response.discount.DiscountResponse;
import com.example.e_commerce.dto.response.discount.PromotionLogResponse;
import com.example.e_commerce.entity.Employee;
import com.example.e_commerce.repository.EmployeeRepository;
import com.example.e_commerce.service.DiscountService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/discounts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DiscountController {

    DiscountService discountService;
    EmployeeRepository employeeRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('PROMOTION_VIEW') or hasAuthority('REPORT_MARKETING_VIEW') or hasRole('MARKETING') or hasRole('ADMIN')")
    public ApiResponse<Page<DiscountResponse>> getAllDiscounts(
            @RequestParam(required = false) Boolean activeOnly,
            @PageableDefault(size = 10, sort = "discountId", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<DiscountResponse> result = discountService.getAllDiscounts(activeOnly, pageable);
        return ApiResponse.<Page<DiscountResponse>>builder()
                .result(result)
                .build();
    }

    @GetMapping("/{discountId}")
    @PreAuthorize("hasAuthority('PROMOTION_VIEW') or hasAuthority('REPORT_MARKETING_VIEW') or hasRole('MARKETING') or hasRole('ADMIN')")
    public ApiResponse<DiscountResponse> getDiscountDetail(@PathVariable Long discountId) {
        DiscountResponse result = discountService.getDiscountDetail(discountId);
        return ApiResponse.<DiscountResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PROMOTION_CREATE') or hasRole('MARKETING') or hasRole('ADMIN')")
    public ApiResponse<DiscountResponse> createDiscount(
            @Valid @RequestBody DiscountRequest request,
            Authentication authentication) {
        String username = authentication != null ? authentication.getName() : null;
        String employeeId = null;
        if (username != null) {
            Employee employee = employeeRepository.findByUsername(username).orElse(null);
            if (employee != null) {
                employeeId = employee.getEmployeeId();
            }
        }

        DiscountResponse result = discountService.createDiscount(request, employeeId);
        return ApiResponse.<DiscountResponse>builder()
                .result(result)
                .message("Tạo mã giảm giá thành công")
                .build();
    }

    @PutMapping("/{discountId}")
    @PreAuthorize("hasAuthority('PROMOTION_UPDATE') or hasRole('MARKETING') or hasRole('ADMIN')")
    public ApiResponse<DiscountResponse> updateDiscount(
            @PathVariable Long discountId,
            @Valid @RequestBody DiscountRequest request,
            Authentication authentication) {
        String username = authentication != null ? authentication.getName() : null;
        String employeeId = null;
        if (username != null) {
            Employee employee = employeeRepository.findByUsername(username).orElse(null);
            if (employee != null) {
                employeeId = employee.getEmployeeId();
            }
        }

        DiscountResponse result = discountService.updateDiscount(discountId, request, employeeId);
        return ApiResponse.<DiscountResponse>builder()
                .result(result)
                .message("Cập nhật mã giảm giá thành công")
                .build();
    }

    @DeleteMapping("/{discountId}")
    @PreAuthorize("hasAuthority('PROMOTION_DELETE') or hasRole('MARKETING') or hasRole('ADMIN')")
    public ApiResponse<Void> deleteDiscount(
            @PathVariable Long discountId,
            Authentication authentication) {
        String username = authentication != null ? authentication.getName() : null;
        String employeeId = null;
        if (username != null) {
            Employee employee = employeeRepository.findByUsername(username).orElse(null);
            if (employee != null) {
                employeeId = employee.getEmployeeId();
            }
        }

        discountService.deleteDiscount(discountId, employeeId);
        return ApiResponse.<Void>builder()
                .message("Xóa mã giảm giá thành công")
                .build();
    }

    @GetMapping("/logs")
    @PreAuthorize("hasAuthority('PROMOTION_VIEW') or hasAuthority('REPORT_MARKETING_VIEW') or hasRole('MARKETING') or hasRole('ADMIN')")
    public ApiResponse<Page<PromotionLogResponse>> getPromotionLogs(
            @RequestParam(required = false) Long discountId,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<PromotionLogResponse> result = discountService.getPromotionLogs(discountId, pageable);
        return ApiResponse.<Page<PromotionLogResponse>>builder()
                .result(result)
                .build();
    }

    @GetMapping("/{discountId}/logs")
    @PreAuthorize("hasAuthority('PROMOTION_VIEW') or hasAuthority('REPORT_MARKETING_VIEW') or hasRole('MARKETING') or hasRole('ADMIN')")
    public ApiResponse<Page<PromotionLogResponse>> getDiscountLogs(
            @PathVariable Long discountId,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<PromotionLogResponse> result = discountService.getPromotionLogs(discountId, pageable);
        return ApiResponse.<Page<PromotionLogResponse>>builder()
                .result(result)
                .build();
    }
}

