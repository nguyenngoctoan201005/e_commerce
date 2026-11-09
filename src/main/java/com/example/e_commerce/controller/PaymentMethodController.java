package com.example.e_commerce.controller;

import com.example.e_commerce.dto.request.ApiResponse;
import com.example.e_commerce.dto.request.PaymentMethodRequest;
import com.example.e_commerce.dto.response.PaymentMethodResponse;
import com.example.e_commerce.service.PaymentMethodService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment_methods")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentMethodController {
    PaymentMethodService paymentMethodService;

    @PostMapping
    @PreAuthorize("hasAuthority('PAYMENT_METHOD_MANAGE') or hasRole('ADMIN')")
    public ApiResponse<PaymentMethodResponse> createPaymentMethod(@Valid @RequestBody PaymentMethodRequest request){
        PaymentMethodResponse response = paymentMethodService.createPaymentMethod(request);
        return ApiResponse.<PaymentMethodResponse>builder()
                .result(response)
                .build();
    }

    @GetMapping("/{paymentMethodId}")
    public ApiResponse<PaymentMethodResponse> getPaymentMethod(@PathVariable Long paymentMethodId){
        PaymentMethodResponse paymentMethodResponse = paymentMethodService.getPaymentMethod(paymentMethodId);
        return ApiResponse.<PaymentMethodResponse>builder()
                .result(paymentMethodResponse)
                .build();
    }

    @GetMapping
    public ApiResponse<List<PaymentMethodResponse>> getPaymentMethods(){
        List<PaymentMethodResponse> paymentMethods = paymentMethodService.getPaymentMethods();
        return ApiResponse.<List<PaymentMethodResponse>>builder()
                .result(paymentMethods)
                .build();
    }

    @DeleteMapping("/{paymentMethodId}")
    @PreAuthorize("hasAuthority('PAYMENT_METHOD_MANAGE') or hasRole('ADMIN')")
    public ApiResponse<String> deletePaymentMethods(@PathVariable Long paymentMethodId){
        String result = paymentMethodService.deletePaymentMethod(paymentMethodId);
        return ApiResponse.<String>builder()
                .result(result)
                .build();
    }

    @PutMapping("/{paymentMethodId}")
    @PreAuthorize("hasAuthority('PAYMENT_METHOD_MANAGE') or hasRole('ADMIN')")
    public ApiResponse<PaymentMethodResponse> updatePaymentMethods(
            @PathVariable Long paymentMethodId,
            @Valid @RequestBody PaymentMethodRequest request
    ){
        PaymentMethodResponse response = paymentMethodService.updatePaymentMethod(paymentMethodId, request);
        return ApiResponse.<PaymentMethodResponse>builder()
                .result(response)
                .build();
    }

}
