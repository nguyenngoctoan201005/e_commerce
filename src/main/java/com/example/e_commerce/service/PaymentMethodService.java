package com.example.e_commerce.service;

import com.example.e_commerce.dto.request.PaymentMethodRequest;
import com.example.e_commerce.dto.response.PaymentMethodResponse;
import com.example.e_commerce.entity.PaymentMethod;
import com.example.e_commerce.enums.ErrorCode;
import com.example.e_commerce.exception.AppException;
import com.example.e_commerce.mapper.PaymentMethodMapper;
import com.example.e_commerce.repository.OrderRepository;
import com.example.e_commerce.repository.PaymentMethodRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentMethodService {
    PaymentMethodRepository paymentMethodRepository;
    PaymentMethodMapper paymentMethodMapper;
    OrderRepository orderRepository;

    @Transactional
    public PaymentMethodResponse createPaymentMethod(PaymentMethodRequest request){
        if (paymentMethodRepository.existsByPaymentMethodName(request.getPaymentMethodName())) {
            throw new AppException(ErrorCode.PAYMENT_EXISTED);
        }
        PaymentMethod paymentMethod = paymentMethodMapper.toPaymentMethod(request);
        return paymentMethodMapper.toPaymentMethodResponse(
                paymentMethodRepository.save(paymentMethod)
        );
    }

    public PaymentMethodResponse getPaymentMethod(Long id){
        PaymentMethod paymentMethod = paymentMethodRepository.findById(id).orElseThrow(()->new AppException(ErrorCode.PAYMENT_NOT_EXISTED));
        return paymentMethodMapper.toPaymentMethodResponse(paymentMethod);
    }

    public List<PaymentMethodResponse> getPaymentMethods(){
        List<PaymentMethodResponse> paymentMethods = paymentMethodRepository
                .findAll()
                .stream()
                .map(paymentMethodMapper :: toPaymentMethodResponse)
                .toList();
        return paymentMethods;
    }

    @Transactional
    public PaymentMethodResponse updatePaymentMethod(Long id ,PaymentMethodRequest request){
        PaymentMethod paymentMethod = paymentMethodRepository.findById(id)
                .orElseThrow(()-> new AppException(ErrorCode.PAYMENT_NOT_EXISTED));

        if (!paymentMethod.getPaymentMethodName().equalsIgnoreCase(request.getPaymentMethodName())
                && paymentMethodRepository.existsByPaymentMethodName(request.getPaymentMethodName())) {
            throw new AppException(ErrorCode.PAYMENT_EXISTED);
        }

        paymentMethodMapper.updatePaymentMethod(paymentMethod , request);
        return paymentMethodMapper.toPaymentMethodResponse(paymentMethodRepository.save(paymentMethod));
    }

    @Transactional
    public String deletePaymentMethod(Long id){
        if (!paymentMethodRepository.existsById(id)) {
            throw new AppException(ErrorCode.PAYMENT_NOT_EXISTED);
        }
        if (orderRepository.existsByPaymentMethod_PaymentMethodId(id)) {
            throw new AppException(ErrorCode.PAYMENT_METHOD_IN_USE);
        }
        paymentMethodRepository.deleteById(id);
        return "deleted payment method";
    }

    public PaymentMethod getPaymentMethodById(Long id){
        return paymentMethodRepository.findById(id)
                .orElseThrow(()-> new AppException(ErrorCode.PAYMENT_NOT_EXISTED));
    }


}
