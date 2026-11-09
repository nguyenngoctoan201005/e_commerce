package com.example.e_commerce.dto.request.review;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReplyReviewRequest {
    @NotBlank(message = "Nội dung phản hồi không được để trống")
    String replyComment;
}

