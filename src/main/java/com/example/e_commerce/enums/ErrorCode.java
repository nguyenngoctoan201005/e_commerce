package com.example.e_commerce.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {

    // 9000s: SYSTEM & AUTHENTICATION
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(9001, "Invalid message key", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(9002, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(9003, "You do not have permission", HttpStatus.FORBIDDEN),

    // 1000s: CATEGORY & CATEGORY TYPE
    CATEGORY_NOT_EXISTED(1001, "Category does not exist", HttpStatus.NOT_FOUND),
    CATEGORY_EXISTED(1002, "Category already exists", HttpStatus.BAD_REQUEST),
    CATEGORY_HAS_PRODUCTS(1003, "Category contains products, cannot be deleted", HttpStatus.BAD_REQUEST),
    CATEGORY_TYPE_NOT_EXISTED(1004, "Category type does not exist", HttpStatus.NOT_FOUND),
    CATEGORY_TYPE_EXISTED(1005, "Category type already exists", HttpStatus.BAD_REQUEST),
    CATEGORY_TYPE_HAS_PRODUCTS(1006, "Category type contains products, cannot be deleted", HttpStatus.BAD_REQUEST),

    // 1100s: PAYMENT METHOD
    PAYMENT_NOT_EXISTED(1101, "Payment method does not exist", HttpStatus.NOT_FOUND),
    PAYMENT_EXISTED(1102, "Payment method already exists", HttpStatus.BAD_REQUEST),
    PAYMENT_METHOD_IN_USE(1103, "Payment method is associated with existing orders and cannot be deleted", HttpStatus.BAD_REQUEST),

    // 1200s: PRODUCT
    PRODUCT_NOT_EXISTED(1201, "Product does not exist", HttpStatus.NOT_FOUND),
    PRODUCT_NOT_IN_CART(1202, "Product is not in the cart", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_FOUND_IN_WAREHOUSE(1203, "Product not found in warehouse", HttpStatus.NOT_FOUND),

    // 1300s: CART
    CART_NOT_EXISTED(1301, "Cart does not exist", HttpStatus.NOT_FOUND),
    INVALID_QUANTITY(1302, "Quantity must be greater than 0", HttpStatus.BAD_REQUEST),
    OUT_OF_STOCK(1303, "Product is out of stock", HttpStatus.BAD_REQUEST),
    CART_IS_EMPTY(1304, "Cart is empty", HttpStatus.BAD_REQUEST),

    // 1400s: CUSTOMER & USER VALIDATION
    CUSTOMER_NOT_EXISTED(1401, "Customer does not exist", HttpStatus.NOT_FOUND),
    EMAIL_EXISTED(1402, "Email has already been used", HttpStatus.BAD_REQUEST),
    INCORRECT_PASSWORD(1403, "Incorrect password", HttpStatus.BAD_REQUEST),
    NEW_PASSWORD_SAME_AS_OLD(1404, "New password must be different from old password", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1405, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1406, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_DOB(1407, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),

    // 1500s: EMPLOYEE & USER
    EMPLOYEE_NOT_EXISTED(1501, "Employee does not exist", HttpStatus.NOT_FOUND),
    DONT_HAVE_EMPLOYEE(1502, "No employees found in the list", HttpStatus.NOT_FOUND),
    USER_EXISTED(1503, "User already exists", HttpStatus.BAD_REQUEST),

    // 1600s: DISCOUNT
    DISCOUNT_NOT_EXISTED(1601, "Discount does not exist", HttpStatus.NOT_FOUND),
    DISCOUNT_OUT_OF_STOCK(1602, "Discount is out of stock", HttpStatus.BAD_REQUEST),
    DISCOUNT_IN_USE(1603, "Discount cannot be deleted because it is already used in orders", HttpStatus.BAD_REQUEST),
    INVALID_DISCOUNT_DATE(1604, "End date must be after start date", HttpStatus.BAD_REQUEST),

    // 1700s: LOCATION & ADDRESS (DISTRICT, PROVINCE)
    DISTRICT_NOT_EXISTED(1701, "District does not exist", HttpStatus.NOT_FOUND),
    PROVINCE_NOT_EXISTED(1702, "Province does not exist", HttpStatus.NOT_FOUND),
    LOCATION_NOT_EXISTED(1703, "Location does not exist", HttpStatus.NOT_FOUND),
    LOCATION_IS_USING(1704, "Location cannot be deleted because stock quantity > 0", HttpStatus.BAD_REQUEST),
    LOCATION_MUST_BE_DIFFERENT(1705, "Locations must be different", HttpStatus.BAD_REQUEST),
    LOCATION_NOT_IN_WAREHOUSE(1706, "Location does not belong to this warehouse", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_STOCK_IN_LOCATION(1707, "Insufficient stock in source location", HttpStatus.BAD_REQUEST),

    // 1800s: ORDER
    ORDER_NOT_EXISTED(1801, "Order does not exist", HttpStatus.NOT_FOUND),
    ORDER_CAN_NOT_CANCEL(1802, "Order cannot be canceled", HttpStatus.BAD_REQUEST),
    INVALID_ORDER_STATUS(1803, "Invalid order status", HttpStatus.BAD_REQUEST),
    CANNOT_UPDATE_FINALIZED_ORDER(1804, "Cannot update a finalized order", HttpStatus.BAD_REQUEST),
    ORDER_NOT_IN_MANAGED_PROVINCES(1805, "Order does not belong to your managed provinces", HttpStatus.FORBIDDEN),

    // 1900s: WAREHOUSE
    DONT_HAVE_WAREHOUSE_IS_QUALIFIED(1901, "No qualified warehouse found", HttpStatus.NOT_FOUND),
    WAREHOUSE_NOT_EXISTED(1902, "Warehouse does not exist", HttpStatus.NOT_FOUND),
    INVALID_WAREHOUSE_STATUS(1903, "Invalid warehouse status", HttpStatus.BAD_REQUEST),
    IMPORT_NOTE_NOT_EXISTED(1904, "Import note does not exist", HttpStatus.NOT_FOUND),
    EXPORT_NOTE_NOT_EXISTED(1905, "Export note does not exist", HttpStatus.NOT_FOUND),
    INVALID_ACTIVITY_TYPE(1906, "Invalid activity type, expected IMPORT or EXPORT", HttpStatus.BAD_REQUEST),

    // 2000s: ROLE & PERMISSION
    ROLE_NOT_EXISTED(2001, "Role does not exist", HttpStatus.NOT_FOUND),
    HAVE_ROLE_INVALID(2002, "Input list contains an invalid role", HttpStatus.BAD_REQUEST),
    PERMISSION_NOT_EXISTED(2003, "Permission does not exist", HttpStatus.NOT_FOUND),

    // 2100s: REVIEW
    REVIEW_NOT_EXISTED(2101, "Review does not exist", HttpStatus.NOT_FOUND),
    REVIEW_ALREADY_REPLIED(2102, "Review has already been replied to", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}