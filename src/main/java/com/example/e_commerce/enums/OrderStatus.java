package com.example.e_commerce.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {
    CHO_XAC_NHAN("Chờ xác nhận"),
    DA_XAC_NHAN("Đã xác nhận"),
    DANG_GIAO("Đang giao hàng"),
    THANH_CONG("Giao hàng thành công"),
    DA_HUY("Đã hủy đơn");

    private final String description;

    public boolean canTransitionTo(OrderStatus nextStatus) {
        if (this == THANH_CONG || this == DA_HUY) {
            return false;
        }
        return true;
    }
}
