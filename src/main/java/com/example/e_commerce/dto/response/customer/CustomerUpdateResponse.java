package com.example.e_commerce.dto.response.customer;

import lombok.AllArgsConstructor;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerUpdateResponse {
    String email;
    String fullname;
    String address;
    String phoneNumber;
    Long districtId;
    String districtName;
}
