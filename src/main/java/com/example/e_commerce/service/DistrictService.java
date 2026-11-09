package com.example.e_commerce.service;

import com.example.e_commerce.entity.District;
import com.example.e_commerce.enums.ErrorCode;
import com.example.e_commerce.exception.AppException;
import com.example.e_commerce.repository.DiscountRepository;
import com.example.e_commerce.repository.DistrictRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DistrictService {
    DistrictRepository districtRepository;

    public District getDistrictById(Long id){
        return districtRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.DISTRICT_NOT_EXISTED));
    }


}
