package com.example.e_commerce.controller;

import com.example.e_commerce.dto.request.ApiResponse;
import com.example.e_commerce.dto.request.IntrospectRequest;
import com.example.e_commerce.dto.request.LogoutRequest;
import com.example.e_commerce.dto.request.RefreshRequest;
import com.example.e_commerce.dto.request.authentication.AuthenticationCustomerRequest;
import com.example.e_commerce.dto.request.authentication.AuthenticationEmployeeRequest;
import com.example.e_commerce.dto.response.AuthenticationResponse;
import com.example.e_commerce.dto.response.IntrospectResponse;
import com.example.e_commerce.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/customers")
    public ApiResponse<AuthenticationResponse> authenticateCustomer(
            @RequestBody AuthenticationCustomerRequest request) {
        AuthenticationResponse authenticationResponse = authenticationService.authenticateCustomer(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(authenticationResponse)
                .build();
    }

    @PostMapping("/employees")
    public ApiResponse<AuthenticationResponse> authenticateEmployee(
            @RequestBody AuthenticationEmployeeRequest request) {
        AuthenticationResponse authenticationResponse = authenticationService.authenticateEmployee(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(authenticationResponse)
                .build();
    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        IntrospectResponse response = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .result(response)
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder()
                .build();
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthenticationResponse> refreshToken(@RequestBody RefreshRequest request)
            throws ParseException, JOSEException {
        AuthenticationResponse response = authenticationService.refreshToken(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(response)
                .build();
    }

}
