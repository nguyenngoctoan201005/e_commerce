package com.example.e_commerce.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReviewStatus {
    CHO_PHAN_HOI("Chờ phản hồi từ nhân viên"),
    DA_PHAN_HOI("Nhân viên đã phản hồi"),
    AN_DANH_GIA("Ẩn đánh giá");

    String description;
}
