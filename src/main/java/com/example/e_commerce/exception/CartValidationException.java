package com.example.e_commerce.exception;

import com.example.e_commerce.enums.ErrorCode;
import lombok.Getter;
import java.util.List;

@Getter
public class CartValidationException extends RuntimeException {

    private final ErrorCode errorCode;
    private final List<?> invalidItems;

    public CartValidationException(ErrorCode errorCode, List<?> invalidItems) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.invalidItems = invalidItems;
    }
}