package com.example.e_commerce.service;

import com.example.e_commerce.dto.request.location.CreateLocationRequest;
import com.example.e_commerce.dto.request.location.UpdateLocationRequest;
import com.example.e_commerce.dto.response.location.LocationInWarehouseResponse;
import com.example.e_commerce.dto.response.product.ProductStockResponse;
import com.example.e_commerce.dto.response.warehouse.WarehouseItemResponse;
import com.example.e_commerce.entity.*;
import com.example.e_commerce.enums.ErrorCode;
import com.example.e_commerce.exception.AppException;
import com.example.e_commerce.mapper.LocationMapper;
import com.example.e_commerce.repository.LocationDetailRepository;
import com.example.e_commerce.repository.LocationRepository;
import com.example.e_commerce.repository.WarehouseRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LocationService {
    private final WarehouseRepository warehouseRepository;
    LocationRepository locationRepository;
    LocationDetailRepository locationDetailRepository;
    LocationMapper locationMapper;
    public static final String DEFAULT_RETURN_LOCATION_NAME = "KHU_HANG_TRA";


    public List<LocationInWarehouseResponse> getLocationByWarehouseId(Long warehouseId){
        if (!warehouseRepository.existsById(warehouseId)) {
            throw new AppException(ErrorCode.WAREHOUSE_NOT_EXISTED);
        }
        return locationRepository.getLocationByWarehouseId(warehouseId);
    }






    @Transactional
    public LocationInWarehouseResponse createLocation(CreateLocationRequest request) {
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new AppException(ErrorCode.WAREHOUSE_NOT_EXISTED));

        Location location = Location.builder()
                .locationName(request.getLocationName())
                .description(request.getDescription())
                .warehouse(warehouse)
                .build();

        location = locationRepository.save(location);

        LocationInWarehouseResponse response = locationMapper.toLocationInWarehouseResponse(location);
        response.setStockQuantity(0L);

        return response;
    }

    @Transactional
    public LocationInWarehouseResponse updateLocation(Long locationId, UpdateLocationRequest request) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new AppException(ErrorCode.LOCATION_NOT_EXISTED));

        if (request.getWarehouseId() != null &&
                !location.getWarehouse().getWarehouseId().equals(request.getWarehouseId())) {
            Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                    .orElseThrow(() -> new AppException(ErrorCode.WAREHOUSE_NOT_EXISTED));
            location.setWarehouse(warehouse);
        }

        location.setLocationName(request.getLocationName());
        location.setDescription(request.getDescription());

        Long actualStockQuantity = location.getLocationDetails().stream()
                .mapToLong(detail -> detail.getStockQuantity() != null ? detail.getStockQuantity() : 0L)
                .sum();

        LocationInWarehouseResponse response = locationMapper.toLocationInWarehouseResponse(location);
        response.setStockQuantity(actualStockQuantity);

        return response;
    }



    @Transactional
    public void deleteLocation(Long locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new AppException(ErrorCode.LOCATION_NOT_EXISTED));

        Long totalStockQuantity = locationDetailRepository.sumStockQuantityByLocationId(locationId);

        if (totalStockQuantity > 0) {
            throw new AppException(ErrorCode.LOCATION_IS_USING);
        }

        locationDetailRepository.deleteByLocationId(locationId);

        locationRepository.delete(location);
    }




    public List<ProductStockResponse> getStockQuantity(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyList();
        }

        return locationDetailRepository.findTotalStockByProductIds(productIds)
                .stream()
                .map(projection -> ProductStockResponse.builder()
                        .productId(projection.getProductId())
                        .totalStock(projection.getTotalStock())
                        .build())
                .toList();
    }

    public Map<Long, Long> getStockMap(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyMap();
        }

        return locationDetailRepository.findTotalStockByProductIds(productIds)
                .stream()
                .collect(Collectors.toMap(
                        LocationDetailRepository.ProductStockProjection::getProductId,
                        LocationDetailRepository.ProductStockProjection::getTotalStock,
                        (existing, replacement) -> existing
                ));
    }

    public Long countTotalStockByProductId(Long productId) {
        return locationDetailRepository.countTotalStockByProductId(productId);
    }


    public boolean getTotalStockByWarehouseAndProduct(Long warehouseId , List<CartDetail> cartDetailList) {
        for(CartDetail item : cartDetailList){
            Long totalStockInWarehouse = locationDetailRepository.getTotalStockByWarehouseAndProduct(
                    warehouseId,
                    item.getProduct().getProductId()
            );

            if (totalStockInWarehouse < item.getQuantity()) {
                return false;
            }
        }
        return true;
    }

    @Transactional
    public void deductStock(Long warehouseId, Map<Long, Long> productQuantityMap) {
        List<Long> sortedProductIds = productQuantityMap.keySet().stream()
                .sorted()
                .toList();

        for (Long productId : sortedProductIds) {
            Long remainingToDeduct = productQuantityMap.get(productId);

            List<LocationDetail> locationDetails = locationDetailRepository
                    .findByWarehouseAndProductWithLock(warehouseId, productId);

            if (locationDetails.isEmpty()) {
                throw new AppException(ErrorCode.PRODUCT_NOT_FOUND_IN_WAREHOUSE);
            }


            for (LocationDetail ld : locationDetails) {
                if (remainingToDeduct <= 0) {
                    break;
                }

                Long currentStock = ld.getStockQuantity();
                if (currentStock <= 0) {
                    continue;
                }

                if (currentStock >= remainingToDeduct) {
                    ld.setStockQuantity(currentStock - remainingToDeduct);
                    remainingToDeduct = 0L;
                } else {
                    remainingToDeduct -= currentStock;
                    ld.setStockQuantity(0L);
                }

                locationDetailRepository.save(ld);
            }

            if (remainingToDeduct > 0) {
                throw new AppException(ErrorCode.OUT_OF_STOCK);
            }
        }
    }

    @Transactional
    public void restoreStock(Long warehouseId, Map<Long, Long> productQuantityMap) {
        if (productQuantityMap == null || productQuantityMap.isEmpty()) {
            return;
        }

        Location defaultLocation = locationRepository
                .findByWarehouseWarehouseIdAndLocationName(warehouseId, DEFAULT_RETURN_LOCATION_NAME)
                .orElseThrow(() -> new AppException(ErrorCode.LOCATION_NOT_EXISTED));

        for (Map.Entry<Long, Long> entry : productQuantityMap.entrySet()) {
            Long productId = entry.getKey();
            Long quantityToRestore = entry.getValue();

            if (quantityToRestore <= 0) continue;

            Optional<LocationDetail> optionalDetail = locationDetailRepository
                    .findByLocationLocationIdAndProductProductId(defaultLocation.getLocationId(), productId);

            if (optionalDetail.isPresent()) {
                LocationDetail locationDetail = optionalDetail.get();
                locationDetail.setStockQuantity(locationDetail.getStockQuantity() + quantityToRestore);
            } else {
                LocationDetail newDetail = LocationDetail.builder()
                        .location(defaultLocation)
                        .product(Product.builder().productId(productId).build())
                        .stockQuantity(quantityToRestore)
                        .build();
                locationDetailRepository.save(newDetail);
            }
        }
    }


    Page<WarehouseItemResponse> getWarehouseItems(Long warehouseId, Pageable pageable){
        return locationDetailRepository.getWarehouseItems(warehouseId, pageable);
    }





}
