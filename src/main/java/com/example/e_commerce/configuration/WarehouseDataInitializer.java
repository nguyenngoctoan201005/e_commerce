package com.example.e_commerce.configuration;


import com.example.e_commerce.entity.Location;
import com.example.e_commerce.entity.Warehouse;
import com.example.e_commerce.repository.LocationRepository;
import com.example.e_commerce.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "app.init-sample-data", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class WarehouseDataInitializer implements CommandLineRunner {

    private final WarehouseRepository warehouseRepository;
    private final LocationRepository locationRepository;

    public static final String DEFAULT_RETURN_LOCATION_NAME = "KHU_HANG_TRA";

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Bắt đầu kiểm tra và khởi tạo KHU_HANG_TRA cho các kho...");

        List<Warehouse> warehouses = warehouseRepository.findAll();

        if (warehouses.isEmpty()) {
            log.info("Chưa có kho nào trong hệ thống.");
            return;
        }

        for (Warehouse warehouse : warehouses) {
            boolean isExist = locationRepository.existsByWarehouseWarehouseIdAndLocationName(
                    warehouse.getWarehouseId(),
                    DEFAULT_RETURN_LOCATION_NAME
            );

            if (!isExist) {
                Location returnLocation = Location.builder()
                        .locationName(DEFAULT_RETURN_LOCATION_NAME)
                        .description("Khu vực tiếp nhận và phân loại hàng hủy / trả về mặc định")
                        .warehouse(warehouse)
                        .build();

                locationRepository.save(returnLocation);
                log.info("Đã tạo thành công {} cho kho ID: {}", DEFAULT_RETURN_LOCATION_NAME, warehouse.getWarehouseId());
            }
        }

        log.info("Hoàn tất khởi tạo dữ liệu kho.");
    }
}