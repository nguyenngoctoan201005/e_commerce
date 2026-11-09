package com.example.e_commerce.service;

import com.example.e_commerce.dto.request.review.CreateReviewRequest;
import com.example.e_commerce.dto.response.review.ReviewResponse;
import com.example.e_commerce.entity.Customer;
import com.example.e_commerce.entity.Employee;
import com.example.e_commerce.entity.Product;
import com.example.e_commerce.entity.Review;
import com.example.e_commerce.enums.ErrorCode;
import com.example.e_commerce.enums.ReviewStatus;
import com.example.e_commerce.exception.AppException;
import com.example.e_commerce.mapper.ReviewMapper;
import com.example.e_commerce.repository.CustomerRepository;
import com.example.e_commerce.repository.EmployeeRepository;
import com.example.e_commerce.repository.ProductRepository;
import com.example.e_commerce.repository.ReviewRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewService {
    EmployeeRepository employeeRepository;
    CustomerRepository customerRepository;
    ProductRepository productRepository;
    ReviewRepository reviewRepository;
    ReviewMapper reviewMapper;

    public Page<ReviewResponse> getAllReviews(Pageable pageable){
        return reviewRepository.getAllReviews(pageable);
    }

    public Page<ReviewResponse> getReviewsByProduct(Long productId, Pageable pageable) {
        if (!productRepository.existsById(productId)) {
            throw new AppException(ErrorCode.PRODUCT_NOT_EXISTED);
        }
        return reviewRepository.findReviewsByProductId(productId, pageable);
    }

    @Transactional
    public ReviewResponse createReview(String customerEmail, CreateReviewRequest request) {
        Customer customer = customerRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_EXISTED));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        Review review = new Review();
        review.setCustomer(customer);
        review.setProduct(product);
        review.setRatingStar(request.getRatingStar());
        review.setComment(request.getComment());
        review.setStatus(ReviewStatus.CHO_PHAN_HOI);

        Review savedReview = reviewRepository.save(review);
        return reviewMapper.toReviewResponse(savedReview);
    }

    @Transactional
    public ReviewResponse replyComment(Long reviewId, String employeeId, String replyComment) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_EXISTED));

        if (review.getStatus() != ReviewStatus.CHO_PHAN_HOI) {
            throw new AppException(ErrorCode.REVIEW_ALREADY_REPLIED);
        }

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));

        review.setReplyComment(replyComment);
        review.setEmployee(employee);
        review.setStatus(ReviewStatus.DA_PHAN_HOI);

        return reviewMapper.toReviewResponse(review);
    }

    @Transactional
    public ReviewResponse hiddenReview(Long reviewId, String employeeId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_EXISTED));

        if (review.getStatus() != ReviewStatus.CHO_PHAN_HOI) {
            throw new AppException(ErrorCode.REVIEW_ALREADY_REPLIED);
        }

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));


        review.setEmployee(employee);
        review.setStatus(ReviewStatus.AN_DANH_GIA);

        return reviewMapper.toReviewResponse(review);
    }
}
