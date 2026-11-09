package com.example.e_commerce.dto.response.review;

import com.example.e_commerce.enums.ReviewStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewResponse {
    Long reviewId;
    Integer ratingStar;
    String comment;
    ReviewStatus status;
    String replyComment;

    String customerId;
    String customerName;
    String customerEmail;

    Long productId;
    String productName;

    String employeeId;
    String employeeName;
}
