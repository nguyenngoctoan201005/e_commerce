package com.example.e_commerce.controller;

import com.example.e_commerce.dto.request.ApiResponse;
import com.example.e_commerce.dto.request.location.CreateLocationRequest;
import com.example.e_commerce.dto.request.location.UpdateLocationRequest;
import com.example.e_commerce.dto.response.CategoryTypeResponse;
import com.example.e_commerce.dto.response.location.LocationInWarehouseResponse;
import com.example.e_commerce.service.LocationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LocationController {
    LocationService locationService;

    @GetMapping("/{warehouseId}")
    @PreAuthorize("hasAuthority('LOCATION_VIEW') or hasAuthority('WAREHOUSE_VIEW') or hasRole('ADMIN')")
    public ApiResponse<List<LocationInWarehouseResponse>> getLocationByWarehouseId(@PathVariable Long warehouseId){
        List<LocationInWarehouseResponse> locations = locationService.getLocationByWarehouseId(warehouseId);
        return ApiResponse.<List<LocationInWarehouseResponse>>builder()
                .result(locations )
                .build();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('LOCATION_MANAGE') or hasRole('ADMIN')")
    public ApiResponse<LocationInWarehouseResponse> createLocation(@Valid @RequestBody CreateLocationRequest request){
        LocationInWarehouseResponse location = locationService.createLocation(request);
        return ApiResponse.<LocationInWarehouseResponse>builder()
                .result(location)
                .build();
    }

    @PutMapping("/{locationId}")
    @PreAuthorize("hasAuthority('LOCATION_MANAGE') or hasRole('ADMIN')")
    public ApiResponse<LocationInWarehouseResponse> updateLocation(@PathVariable Long locationId,
                                                                   @Valid @RequestBody UpdateLocationRequest request)
    {
        LocationInWarehouseResponse location = locationService.updateLocation(locationId,request);
        return ApiResponse.<LocationInWarehouseResponse>builder()
                .result(location)
                .build();
    }

    @DeleteMapping("/{locationId}")
    @PreAuthorize("hasAuthority('LOCATION_MANAGE') or hasRole('ADMIN')")
    public ApiResponse<Void> updateLocation(@PathVariable Long locationId) {
        locationService.deleteLocation(locationId);
        return ApiResponse.<Void>builder()
                .message("Xóa vị trí thành công !")
                .build();
    }


}
