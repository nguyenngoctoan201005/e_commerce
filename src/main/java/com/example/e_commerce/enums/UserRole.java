package com.example.e_commerce.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {
    SALE("Nhân viên bán hàng"),
    MARKETING("Nhân viên marketting"),
    WAREHOUSE("Nhân viên kho"),
    ADMIN("Quản trị viên"),
    CUSTOMER("Khách hàng");

    private final String description;
}
