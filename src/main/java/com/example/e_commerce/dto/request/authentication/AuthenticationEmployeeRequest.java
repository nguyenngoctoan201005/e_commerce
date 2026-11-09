package com.example.e_commerce.dto.request.authentication;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationEmployeeRequest {
    String username;
    String password;
}


