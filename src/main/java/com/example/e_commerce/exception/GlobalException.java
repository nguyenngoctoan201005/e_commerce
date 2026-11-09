package com.example.e_commerce.exception;

import com.example.e_commerce.dto.request.ApiResponse;
import com.example.e_commerce.enums.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalException {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.error("Validation error: ", exception);

        Map<String, String> errors = new HashMap<>();
        String primaryMessage = "Dữ liệu đầu vào không hợp lệ";
        ErrorCode errorCode = ErrorCode.INVALID_KEY;

        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        FieldError firstError = exception.getFieldError();
        if (firstError != null && firstError.getDefaultMessage() != null) {
            String defaultMessage = firstError.getDefaultMessage();
            try {
                errorCode = ErrorCode.valueOf(defaultMessage);
                primaryMessage = errorCode.getMessage();
            } catch (IllegalArgumentException | NullPointerException ignored) {
                primaryMessage = defaultMessage;
            }
        }

        ApiResponse<Map<String, String>> apiResponse = ApiResponse.<Map<String, String>>builder()
                .code(errorCode.getCode())
                .message(primaryMessage)
                .result(errors)
                .build();

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
        log.error("Lỗi toàn vẹn dữ liệu / Ràng buộc khóa ngoại: ", exception);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(400)
                .message("Dữ liệu đang được liên kết và sử dụng ở các bảng khác, không thể xóa hoặc thay đổi!")
                .build();

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handlingException(Exception exception) {
        ApiResponse apiResponse = new ApiResponse();
        log.error("Uncategorized error detail: ", exception);

        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        log.error("Lỗi định dạng JSON Request Body: ", exception);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(ErrorCode.INVALID_KEY.getCode())
                .message("Dữ liệu gửi lên không đúng định dạng JSON hoặc sai giá trị Enum")
                .build();

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(apiResponse);
    }

    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            PropertyReferenceException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ApiResponse<?>> handleBadRequestExceptions(Exception exception) {
        log.error("Lỗi Param/Binding từ Client: ", exception);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(400)
                .message("Tham số truyền vào không hợp lệ: " + exception.getMessage())
                .build();

        return ResponseEntity.badRequest().body(apiResponse);
    }


}