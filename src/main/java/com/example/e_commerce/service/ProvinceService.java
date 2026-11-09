package com.example.e_commerce.service;

import com.example.e_commerce.entity.District;
import com.example.e_commerce.entity.Province;
import com.example.e_commerce.enums.ErrorCode;
import com.example.e_commerce.exception.AppException;
import com.example.e_commerce.repository.ProvinceRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.event.internal.ProxyVisitor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProvinceService {
    private final ProvinceRepository provinceRepository;

    public Province findById(Long provinceId){
        return provinceRepository.findById(provinceId)
                .orElseThrow(()-> new AppException(ErrorCode.PROVINCE_NOT_EXISTED));
    }

}
