package com.example.e_commerce.controller;

import com.example.e_commerce.dto.request.ApiResponse;
import com.example.e_commerce.dto.request.review.CreateReviewRequest;
import com.example.e_commerce.dto.response.review.ReviewResponse;
import com.example.e_commerce.entity.Employee;
import com.example.e_commerce.enums.ErrorCode;
import com.example.e_commerce.exception.AppException;
import com.example.e_commerce.repository.EmployeeRepository;
import com.example.e_commerce.service.ReviewService;
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
@RequestMapping("/reviews")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewController {
    ReviewService reviewService;
    EmployeeRepository employeeRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('REVIEW_VIEW') or hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ApiResponse<Page<ReviewResponse>> getAllReviews(@PageableDefault(
            page = 0,
            size = 10,
            sort = "reviewId",
            direction = Sort.Direction.DESC
    ) Pageable pageable){
        return ApiResponse.<Page<ReviewResponse>>builder()
                .result(reviewService.getAllReviews(pageable))
                .build();
    }

    @GetMapping("/product/{productId}")
    public ApiResponse<Page<ReviewResponse>> getReviewsByProduct(
            @PathVariable Long productId,
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "reviewId",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return ApiResponse.<Page<ReviewResponse>>builder()
                .result(reviewService.getReviewsByProduct(productId, pageable))
                .build();
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<ReviewResponse> createReview(
            @Valid @RequestBody CreateReviewRequest request,
            Authentication authentication
    ) {
        String customerEmail = authentication.getName();
        return ApiResponse.<ReviewResponse>builder()
                .result(reviewService.createReview(customerEmail, request))
                .build();
    }

    @PutMapping("/reply/{reviewId}")
    @PreAuthorize("hasAuthority('REVIEW_REPLY') or hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ApiResponse<ReviewResponse> replyComment(
            @PathVariable Long reviewId,
            Authentication authentication,
            @Valid @RequestBody com.example.e_commerce.dto.request.review.ReplyReviewRequest request
    ){
        String username = authentication.getName();
        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(()-> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));

        return ApiResponse.<ReviewResponse>builder()
                .result(reviewService.replyComment(reviewId, employee.getEmployeeId(), request.getReplyComment()))
                .build();
    }

    @PutMapping("/hidden/{reviewId}")
    @PreAuthorize("hasAuthority('REVIEW_MODERATE') or hasRole('ADMIN')")
    public ApiResponse<ReviewResponse> hiddenReview(
            @PathVariable Long reviewId,
            Authentication authentication
    ){
        String username = authentication.getName();
        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(()-> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));

        return ApiResponse.<ReviewResponse>builder()
                .result(reviewService.hiddenReview(reviewId, employee.getEmployeeId()))
                .build();
    }
}
