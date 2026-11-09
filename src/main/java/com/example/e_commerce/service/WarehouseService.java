package com.example.e_commerce.service;

import com.example.e_commerce.dto.request.warehouse.CreateExportNoteRequest;
import com.example.e_commerce.dto.request.warehouse.CreateImportNoteRequest;
import com.example.e_commerce.dto.request.warehouse.ExportNoteItemRequest;
import com.example.e_commerce.dto.request.warehouse.ImportNoteItemRequest;
import com.example.e_commerce.dto.request.warehouse.MoveProductRequest;
import com.example.e_commerce.dto.request.warehouse.WarehouseCreateRequest;
import com.example.e_commerce.dto.request.warehouse.WarehouseUpdateRequest;
import com.example.e_commerce.dto.response.location.ProductInLocationResponse;
import com.example.e_commerce.dto.response.warehouse.*;
import com.example.e_commerce.entity.*;
import com.example.e_commerce.enums.ErrorCode;
import com.example.e_commerce.enums.WarehouseStatus;
import com.example.e_commerce.exception.AppException;
import com.example.e_commerce.mapper.WarehouseMapper;
import com.example.e_commerce.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WarehouseService {
    WarehouseRepository warehouseRepository;
    LocationService locationService;
    ProvinceService provinceService;
    EmployeeService employeeService;
    WarehouseMapper warehouseMapper;
    ProductRepository productRepository;
    LocationDetailRepository locationDetailRepository;
    LocationRepository locationRepository;
    ImportNoteRepository importNoteRepository;
    GoodsReceiptDetailRepository goodsReceiptDetailRepository;
    ExportNoteRepository exportNoteRepository;
    GoodsDeliveryDetailRepository goodsDeliveryDetailRepository;
    EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public Page<WarehouseSummaryResponse> getWarehouses(Pageable pageable) {
        return warehouseRepository.getWarehousesSummary(pageable);
    }

    public Warehouse findByEmployeeId(String employeeId) {
        return warehouseRepository.findByEmployee_EmployeeId(employeeId)
                .orElseThrow(() -> new AppException(ErrorCode.WAREHOUSE_NOT_EXISTED));
    }

    @Transactional(readOnly = true)
    public Page<WarehouseItemResponse> getWarehouseItems(Long warehouseId, Pageable pageable) {
        return locationService.getWarehouseItems(warehouseId, pageable);
    }

    public WarehouseSummaryResponse createWarehouse(WarehouseCreateRequest request) {
        Province province = provinceService.findById(request.getProvinceId());
        Employee employee = employeeService.findEmployeeById(request.getManagerEmployeeId());

        Warehouse warehouse = Warehouse.builder()
                .warehouseName(request.getWarehouseName())
                .status(request.getStatus() != null ? request.getStatus() : WarehouseStatus.ACTIVE)
                .province(province)
                .employee(employee)
                .build();
        warehouse = warehouseRepository.save(warehouse);

        return warehouseMapper.toWarehouseSummaryResponse(warehouse);
    }

    @Transactional
    public WarehouseSummaryResponse updateWarehouse(Long warehouseId, WarehouseUpdateRequest request) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new AppException(ErrorCode.WAREHOUSE_NOT_EXISTED));

        warehouseMapper.updateWarehouse(warehouse, request);

        if (request.getProvinceId() != null) {
            Province province = provinceService.findById(request.getProvinceId());
            warehouse.setProvince(province);
        }

        if (request.getManagerEmployeeId() != null) {
            Employee employee = employeeService.findEmployeeById(request.getManagerEmployeeId());
            warehouse.setEmployee(employee);
        }

        return warehouseRepository.getWarehouseSummaryById(warehouseId)
                .orElseThrow(() -> new AppException(ErrorCode.WAREHOUSE_NOT_EXISTED));
    }

    @Transactional
    public WarehouseSummaryResponse updateStatusWarehouse(Long warehouseId, WarehouseStatus status) {
        if (status == null) {
            throw new AppException(ErrorCode.INVALID_WAREHOUSE_STATUS);
        }

        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new AppException(ErrorCode.WAREHOUSE_NOT_EXISTED));

        warehouse.setStatus(status);

        return warehouseRepository.getWarehouseSummaryById(warehouseId)
                .orElseThrow(() -> new AppException(ErrorCode.WAREHOUSE_NOT_EXISTED));
    }

    public List<Warehouse> findAllByStatus(WarehouseStatus status) {
        List<Warehouse> warehouseList = warehouseRepository.findAllByStatus(status);
        return warehouseList;
    }

    public List<Warehouse> findWarehousesWithEnoughStock(List<CartDetail> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            return Collections.emptyList();
        }

        List<Warehouse> qualifiedWarehouses = null;

        for (CartDetail item : cartItems) {
            List<Warehouse> warehousesForProduct = warehouseRepository.findWarehousesWithStockForProduct(
                    item.getProduct().getProductId(),
                    item.getQuantity());

            if (qualifiedWarehouses == null) {
                qualifiedWarehouses = new ArrayList<>(warehousesForProduct);
            } else {
                qualifiedWarehouses.retainAll(warehousesForProduct);
            }

            if (qualifiedWarehouses.isEmpty()) {
                return Collections.emptyList();
            }
        }

        return qualifiedWarehouses != null ? qualifiedWarehouses : Collections.emptyList();
    }

    public List<ProductInWarehouseResponse> getProductsInWarehouse(Long warehouseId, String employeeId) {
        return warehouseRepository.getProductsInWarehouse(warehouseId, employeeId);
    }

    public ProductDetailInWarehouseResponse getProductDetailInWarehouse(Long warehouseId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        List<ProductInLocationResponse> productList = locationDetailRepository
                .findLocationsByWarehouseAndProduct(warehouseId, productId);

        long totalQuantity = productList.stream()
                .mapToLong(p -> p.getStockQuantity() != null ? p.getStockQuantity() : 0L)
                .sum();

        long totalPosition = productList.size();

        return ProductDetailInWarehouseResponse.builder()
                .productId(product.getProductId())
                .productName(product.getName())
                .totalPosition(totalPosition)
                .totalQuantity(totalQuantity)
                .productList(productList)
                .build();
    }

    @Transactional
    public ProductDetailInWarehouseResponse moveProduct(Long warehouseId, Long productId, MoveProductRequest request) {
        if (productId == null) {
            productId = request.getProductId();
        }
        if (productId == null) {
            throw new AppException(ErrorCode.PRODUCT_NOT_EXISTED);
        }

        warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new AppException(ErrorCode.WAREHOUSE_NOT_EXISTED));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        if (request.getOldLocationId().equals(request.getNewLocationId())) {
            throw new AppException(ErrorCode.LOCATION_MUST_BE_DIFFERENT);
        }

        Location oldLocation = locationRepository.findById(request.getOldLocationId())
                .orElseThrow(() -> new AppException(ErrorCode.LOCATION_NOT_EXISTED));
        if (oldLocation.getWarehouse() == null || !oldLocation.getWarehouse().getWarehouseId().equals(warehouseId)) {
            throw new AppException(ErrorCode.LOCATION_NOT_IN_WAREHOUSE);
        }

        Location newLocation = locationRepository.findById(request.getNewLocationId())
                .orElseThrow(() -> new AppException(ErrorCode.LOCATION_NOT_EXISTED));
        if (newLocation.getWarehouse() == null || !newLocation.getWarehouse().getWarehouseId().equals(warehouseId)) {
            throw new AppException(ErrorCode.LOCATION_NOT_IN_WAREHOUSE);
        }

        LocationDetail oldLocationDetail = locationDetailRepository
                .findByLocationLocationIdAndProductProductId(request.getOldLocationId(), productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND_IN_WAREHOUSE));

        Long currentOldStock = oldLocationDetail.getStockQuantity() != null ? oldLocationDetail.getStockQuantity() : 0L;
        if (currentOldStock < request.getQuantity()) {
            throw new AppException(ErrorCode.INSUFFICIENT_STOCK_IN_LOCATION);
        }

        long remainingStock = currentOldStock - request.getQuantity();
        if (remainingStock == 0) {
            locationDetailRepository.delete(oldLocationDetail);
        } else {
            oldLocationDetail.setStockQuantity(remainingStock);
            locationDetailRepository.save(oldLocationDetail);
        }

        LocationDetail newLocationDetail = locationDetailRepository
                .findByLocationLocationIdAndProductProductId(request.getNewLocationId(), productId)
                .orElse(null);

        if (newLocationDetail != null) {
            Long currentNewStock = newLocationDetail.getStockQuantity() != null ? newLocationDetail.getStockQuantity()
                    : 0L;
            newLocationDetail.setStockQuantity(currentNewStock + request.getQuantity());
            locationDetailRepository.save(newLocationDetail);
        } else {
            newLocationDetail = LocationDetail.builder()
                    .location(newLocation)
                    .product(product)
                    .stockQuantity(request.getQuantity())
                    .build();
            locationDetailRepository.save(newLocationDetail);
        }

        locationDetailRepository.flush();

        return getProductDetailInWarehouse(warehouseId, productId);
    }

    @Transactional
    public ImportNoteResponse createImportNote(Long warehouseId, String employeeId, CreateImportNoteRequest request) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new AppException(ErrorCode.WAREHOUSE_NOT_EXISTED));

        Employee employee = null;
        if (employeeId != null && !employeeId.isBlank()) {
            employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));
        }

        ImportNote importNote = ImportNote.builder()
                .import_date(LocalDateTime.now())
                .supplier(request.getSupplier())
                .warehouse(warehouse)
                .employee(employee)
                .goodsReceiptDetails(new HashSet<>())
                .build();

        ImportNote savedImportNote = importNoteRepository.save(importNote);

        long totalQuantity = 0L;
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<ImportNoteItemResponse> itemResponses = new ArrayList<>();

        for (ImportNoteItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

            Location location = locationRepository.findById(itemRequest.getLocationId())
                    .orElseThrow(() -> new AppException(ErrorCode.LOCATION_NOT_EXISTED));

            if (location.getWarehouse() == null || !location.getWarehouse().getWarehouseId().equals(warehouseId)) {
                throw new AppException(ErrorCode.LOCATION_NOT_IN_WAREHOUSE);
            }

            BigDecimal unitPrice = itemRequest.getImportPrice() != null
                    ? itemRequest.getImportPrice()
                    : (product.getUnitPrice() != null ? product.getUnitPrice() : BigDecimal.ZERO);

            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            totalQuantity += itemRequest.getQuantity();
            totalAmount = totalAmount.add(lineTotal);

            GoodsReceiptDetail receiptDetail = GoodsReceiptDetail.builder()
                    .importNote(savedImportNote)
                    .product(product)
                    .location(location)
                    .import_quantity(itemRequest.getQuantity().intValue())
                    .import_price(unitPrice)
                    .build();

            goodsReceiptDetailRepository.save(receiptDetail);

            LocationDetail locationDetail = locationDetailRepository
                    .findByLocationLocationIdAndProductProductId(location.getLocationId(), product.getProductId())
                    .orElse(null);

            if (locationDetail != null) {
                Long currentStock = locationDetail.getStockQuantity() != null ? locationDetail.getStockQuantity() : 0L;
                locationDetail.setStockQuantity(currentStock + itemRequest.getQuantity());
                locationDetailRepository.save(locationDetail);
            } else {
                LocationDetail newDetail = LocationDetail.builder()
                        .location(location)
                        .product(product)
                        .stockQuantity(itemRequest.getQuantity())
                        .build();
                locationDetailRepository.save(newDetail);
            }

            itemResponses.add(ImportNoteItemResponse.builder()
                    .receiptDetailId(receiptDetail.getReceiptDetailId())
                    .productId(product.getProductId())
                    .productName(product.getName())
                    .locationId(location.getLocationId())
                    .locationName(location.getLocationName())
                    .quantity(itemRequest.getQuantity())
                    .importPrice(unitPrice)
                    .totalPrice(lineTotal)
                    .build());
        }

        locationDetailRepository.flush();

        return ImportNoteResponse.builder()
                .receiptId(savedImportNote.getReceiptId())
                .importDate(savedImportNote.getImport_date())
                .supplier(savedImportNote.getSupplier())
                .warehouseId(warehouse.getWarehouseId())
                .warehouseName(warehouse.getWarehouseName())
                .employeeId(employee != null ? employee.getEmployeeId() : null)
                .employeeName(employee != null ? employee.getFullname() : null)
                .totalQuantity(totalQuantity)
                .totalAmount(totalAmount)
                .items(itemResponses)
                .build();
    }

    @Transactional(readOnly = true)
    public ImportNoteResponse getImportNoteDetail(Long receiptId) {
        ImportNote importNote = importNoteRepository.findById(receiptId)
                .orElseThrow(() -> new AppException(ErrorCode.IMPORT_NOTE_NOT_EXISTED));

        List<GoodsReceiptDetail> details = goodsReceiptDetailRepository.findByImportNote_ReceiptId(receiptId);

        long totalQuantity = 0L;
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<ImportNoteItemResponse> itemResponses = new ArrayList<>();

        for (GoodsReceiptDetail detail : details) {
            long qty = detail.getImport_quantity() != null ? detail.getImport_quantity().longValue() : 0L;
            BigDecimal price = detail.getImport_price() != null ? detail.getImport_price() : BigDecimal.ZERO;
            BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(qty));

            totalQuantity += qty;
            totalAmount = totalAmount.add(lineTotal);

            itemResponses.add(ImportNoteItemResponse.builder()
                    .receiptDetailId(detail.getReceiptDetailId())
                    .productId(detail.getProduct() != null ? detail.getProduct().getProductId() : null)
                    .productName(detail.getProduct() != null ? detail.getProduct().getName() : null)
                    .locationId(detail.getLocation() != null ? detail.getLocation().getLocationId() : null)
                    .locationName(detail.getLocation() != null ? detail.getLocation().getLocationName() : null)
                    .quantity(qty)
                    .importPrice(price)
                    .totalPrice(lineTotal)
                    .build());
        }

        return ImportNoteResponse.builder()
                .receiptId(importNote.getReceiptId())
                .importDate(importNote.getImport_date())
                .supplier(importNote.getSupplier())
                .warehouseId(importNote.getWarehouse() != null ? importNote.getWarehouse().getWarehouseId() : null)
                .warehouseName(importNote.getWarehouse() != null ? importNote.getWarehouse().getWarehouseName() : null)
                .employeeId(importNote.getEmployee() != null ? importNote.getEmployee().getEmployeeId() : null)
                .employeeName(importNote.getEmployee() != null ? importNote.getEmployee().getFullname() : null)
                .totalQuantity(totalQuantity)
                .totalAmount(totalAmount)
                .items(itemResponses)
                .build();
    }

    @Transactional
    public ExportNoteResponse createExportNote(Long warehouseId, String employeeId, CreateExportNoteRequest request) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new AppException(ErrorCode.WAREHOUSE_NOT_EXISTED));

        Employee employee = null;
        if (employeeId != null && !employeeId.isBlank()) {
            employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));
        }

        ExportNote exportNote = ExportNote.builder()
                .export_date(LocalDateTime.now())
                .purpose(request.getPurpose())
                .warehouse(warehouse)
                .employee(employee)
                .goodsDeliveryDetails(new HashSet<>())
                .build();

        ExportNote savedExportNote = exportNoteRepository.save(exportNote);

        long totalQuantity = 0L;
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<ExportNoteItemResponse> itemResponses = new ArrayList<>();

        for (ExportNoteItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

            Location location = locationRepository.findById(itemRequest.getLocationId())
                    .orElseThrow(() -> new AppException(ErrorCode.LOCATION_NOT_EXISTED));

            if (location.getWarehouse() == null || !location.getWarehouse().getWarehouseId().equals(warehouseId)) {
                throw new AppException(ErrorCode.LOCATION_NOT_IN_WAREHOUSE);
            }

            LocationDetail locationDetail = locationDetailRepository
                    .findByLocationLocationIdAndProductProductId(location.getLocationId(), product.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND_IN_WAREHOUSE));

            Long currentStock = locationDetail.getStockQuantity() != null ? locationDetail.getStockQuantity() : 0L;
            if (currentStock < itemRequest.getQuantity()) {
                throw new AppException(ErrorCode.INSUFFICIENT_STOCK_IN_LOCATION);
            }

            BigDecimal unitPrice = itemRequest.getExportPrice() != null
                    ? itemRequest.getExportPrice()
                    : (product.getUnitPrice() != null ? product.getUnitPrice() : BigDecimal.ZERO);

            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            totalQuantity += itemRequest.getQuantity();
            totalAmount = totalAmount.add(lineTotal);

            GoodsDeliveryDetail deliveryDetail = GoodsDeliveryDetail.builder()
                    .exportNote(savedExportNote)
                    .product(product)
                    .location(location)
                    .export_quantity(itemRequest.getQuantity().intValue())
                    .export_price(unitPrice)
                    .build();

            goodsDeliveryDetailRepository.save(deliveryDetail);

            long remainingStock = currentStock - itemRequest.getQuantity();
            if (remainingStock == 0) {
                locationDetailRepository.delete(locationDetail);
            } else {
                locationDetail.setStockQuantity(remainingStock);
                locationDetailRepository.save(locationDetail);
            }

            itemResponses.add(ExportNoteItemResponse.builder()
                    .deliveryDetailId(deliveryDetail.getDeliveryDetailId())
                    .productId(product.getProductId())
                    .productName(product.getName())
                    .locationId(location.getLocationId())
                    .locationName(location.getLocationName())
                    .quantity(itemRequest.getQuantity())
                    .exportPrice(unitPrice)
                    .totalPrice(lineTotal)
                    .build());
        }

        locationDetailRepository.flush();

        return ExportNoteResponse.builder()
                .deliveryId(savedExportNote.getDeliveryId())
                .exportDate(savedExportNote.getExport_date())
                .purpose(savedExportNote.getPurpose())
                .warehouseId(warehouse.getWarehouseId())
                .warehouseName(warehouse.getWarehouseName())
                .employeeId(employee != null ? employee.getEmployeeId() : null)
                .employeeName(employee != null ? employee.getFullname() : null)
                .totalQuantity(totalQuantity)
                .totalAmount(totalAmount)
                .items(itemResponses)
                .build();
    }

    @Transactional(readOnly = true)
    public ExportNoteResponse getExportNoteDetail(Long deliveryId) {
        ExportNote exportNote = exportNoteRepository.findById(deliveryId)
                .orElseThrow(() -> new AppException(ErrorCode.EXPORT_NOTE_NOT_EXISTED));

        List<GoodsDeliveryDetail> details = goodsDeliveryDetailRepository.findByExportNote_DeliveryId(deliveryId);

        long totalQuantity = 0L;
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<ExportNoteItemResponse> itemResponses = new ArrayList<>();

        for (GoodsDeliveryDetail detail : details) {
            long qty = detail.getExport_quantity() != null ? detail.getExport_quantity().longValue() : 0L;
            BigDecimal price = detail.getExport_price() != null ? detail.getExport_price() : BigDecimal.ZERO;
            BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(qty));

            totalQuantity += qty;
            totalAmount = totalAmount.add(lineTotal);

            itemResponses.add(ExportNoteItemResponse.builder()
                    .deliveryDetailId(detail.getDeliveryDetailId())
                    .productId(detail.getProduct() != null ? detail.getProduct().getProductId() : null)
                    .productName(detail.getProduct() != null ? detail.getProduct().getName() : null)
                    .locationId(detail.getLocation() != null ? detail.getLocation().getLocationId() : null)
                    .locationName(detail.getLocation() != null ? detail.getLocation().getLocationName() : null)
                    .quantity(qty)
                    .exportPrice(price)
                    .totalPrice(lineTotal)
                    .build());
        }

        return ExportNoteResponse.builder()
                .deliveryId(exportNote.getDeliveryId())
                .exportDate(exportNote.getExport_date())
                .purpose(exportNote.getPurpose())
                .warehouseId(exportNote.getWarehouse() != null ? exportNote.getWarehouse().getWarehouseId() : null)
                .warehouseName(exportNote.getWarehouse() != null ? exportNote.getWarehouse().getWarehouseName() : null)
                .employeeId(exportNote.getEmployee() != null ? exportNote.getEmployee().getEmployeeId() : null)
                .employeeName(exportNote.getEmployee() != null ? exportNote.getEmployee().getFullname() : null)
                .totalQuantity(totalQuantity)
                .totalAmount(totalAmount)
                .items(itemResponses)
                .build();
    }

    private WarehouseActivityResponse mapImportNoteToActivity(ImportNote note) {
        long totalQuantity = 0L;
        BigDecimal totalAmount = BigDecimal.ZERO;
        if (note.getGoodsReceiptDetails() != null) {
            for (GoodsReceiptDetail d : note.getGoodsReceiptDetails()) {
                long qty = d.getImport_quantity() != null ? d.getImport_quantity().longValue() : 0L;
                BigDecimal price = d.getImport_price() != null ? d.getImport_price() : BigDecimal.ZERO;
                totalQuantity += qty;
                totalAmount = totalAmount.add(price.multiply(BigDecimal.valueOf(qty)));
            }
        }
        int productTypes = note.getGoodsReceiptDetails() != null ? note.getGoodsReceiptDetails().size() : 0;

        return WarehouseActivityResponse.builder()
                .activityType("IMPORT")
                .activityId(note.getReceiptId())
                .activityCode("PN-" + note.getReceiptId())
                .timestamp(note.getImport_date())
                .description(note.getSupplier())
                .warehouseId(note.getWarehouse() != null ? note.getWarehouse().getWarehouseId() : null)
                .warehouseName(note.getWarehouse() != null ? note.getWarehouse().getWarehouseName() : null)
                .employeeId(note.getEmployee() != null ? note.getEmployee().getEmployeeId() : null)
                .employeeName(note.getEmployee() != null ? note.getEmployee().getFullname() : null)
                .totalQuantity(totalQuantity)
                .totalAmount(totalAmount)
                .totalProductTypes(productTypes)
                .build();
    }

    private WarehouseActivityResponse mapExportNoteToActivity(ExportNote note) {
        long totalQuantity = 0L;
        BigDecimal totalAmount = BigDecimal.ZERO;
        if (note.getGoodsDeliveryDetails() != null) {
            for (GoodsDeliveryDetail d : note.getGoodsDeliveryDetails()) {
                long qty = d.getExport_quantity() != null ? d.getExport_quantity().longValue() : 0L;
                BigDecimal price = d.getExport_price() != null ? d.getExport_price() : BigDecimal.ZERO;
                totalQuantity += qty;
                totalAmount = totalAmount.add(price.multiply(BigDecimal.valueOf(qty)));
            }
        }
        int productTypes = note.getGoodsDeliveryDetails() != null ? note.getGoodsDeliveryDetails().size() : 0;

        return WarehouseActivityResponse.builder()
                .activityType("EXPORT")
                .activityId(note.getDeliveryId())
                .activityCode("PX-" + note.getDeliveryId())
                .timestamp(note.getExport_date())
                .description(note.getPurpose())
                .warehouseId(note.getWarehouse() != null ? note.getWarehouse().getWarehouseId() : null)
                .warehouseName(note.getWarehouse() != null ? note.getWarehouse().getWarehouseName() : null)
                .employeeId(note.getEmployee() != null ? note.getEmployee().getEmployeeId() : null)
                .employeeName(note.getEmployee() != null ? note.getEmployee().getFullname() : null)
                .totalQuantity(totalQuantity)
                .totalAmount(totalAmount)
                .totalProductTypes(productTypes)
                .build();
    }

    @Transactional(readOnly = true)
    public Page<WarehouseActivityResponse> getEmployeeActivities(String employeeId, String type, Pageable pageable) {
        if (employeeId != null && !employeeId.isBlank()) {
            if (!employeeRepository.existsById(employeeId)) {
                throw new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED);
            }
        }

        List<WarehouseActivityResponse> activities = new ArrayList<>();
        boolean includeImport = type == null || type.isBlank() || type.equalsIgnoreCase("ALL") || type.equalsIgnoreCase("IMPORT");
        boolean includeExport = type == null || type.isBlank() || type.equalsIgnoreCase("ALL") || type.equalsIgnoreCase("EXPORT");

        if (includeImport) {
            List<ImportNote> importNotes = importNoteRepository.findByEmployeeId(employeeId);
            for (ImportNote note : importNotes) {
                activities.add(mapImportNoteToActivity(note));
            }
        }

        if (includeExport) {
            List<ExportNote> exportNotes = exportNoteRepository.findByEmployeeId(employeeId);
            for (ExportNote note : exportNotes) {
                activities.add(mapExportNoteToActivity(note));
            }
        }

        activities.sort((a, b) -> {
            if (a.getTimestamp() == null && b.getTimestamp() == null) return 0;
            if (a.getTimestamp() == null) return 1;
            if (b.getTimestamp() == null) return -1;
            return b.getTimestamp().compareTo(a.getTimestamp());
        });

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), activities.size());
        List<WarehouseActivityResponse> pageContent = (start <= end && start < activities.size())
                ? activities.subList(start, end)
                : Collections.emptyList();

        return new PageImpl<>(pageContent, pageable, activities.size());
    }

    @Transactional(readOnly = true)
    public Page<WarehouseActivityResponse> getWarehouseActivities(Long warehouseId, String type, Pageable pageable) {
        if (!warehouseRepository.existsById(warehouseId)) {
            throw new AppException(ErrorCode.WAREHOUSE_NOT_EXISTED);
        }

        List<WarehouseActivityResponse> activities = new ArrayList<>();
        boolean includeImport = type == null || type.isBlank() || type.equalsIgnoreCase("ALL") || type.equalsIgnoreCase("IMPORT");
        boolean includeExport = type == null || type.isBlank() || type.equalsIgnoreCase("ALL") || type.equalsIgnoreCase("EXPORT");

        if (includeImport) {
            List<ImportNote> importNotes = importNoteRepository.findByWarehouseId(warehouseId);
            for (ImportNote note : importNotes) {
                activities.add(mapImportNoteToActivity(note));
            }
        }

        if (includeExport) {
            List<ExportNote> exportNotes = exportNoteRepository.findByWarehouseId(warehouseId);
            for (ExportNote note : exportNotes) {
                activities.add(mapExportNoteToActivity(note));
            }
        }

        activities.sort((a, b) -> {
            if (a.getTimestamp() == null && b.getTimestamp() == null) return 0;
            if (a.getTimestamp() == null) return 1;
            if (b.getTimestamp() == null) return -1;
            return b.getTimestamp().compareTo(a.getTimestamp());
        });

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), activities.size());
        List<WarehouseActivityResponse> pageContent = (start <= end && start < activities.size())
                ? activities.subList(start, end)
                : Collections.emptyList();

        return new PageImpl<>(pageContent, pageable, activities.size());
    }

    @Transactional(readOnly = true)
    public WarehouseActivityDetailResponse getActivityDetail(String type, Long id) {
        if (type == null || type.isBlank()) {
            throw new AppException(ErrorCode.INVALID_ACTIVITY_TYPE);
        }

        if (type.equalsIgnoreCase("IMPORT")) {
            ImportNote importNote = importNoteRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.IMPORT_NOTE_NOT_EXISTED));

            List<GoodsReceiptDetail> details = goodsReceiptDetailRepository.findByImportNote_ReceiptId(id);
            long totalQuantity = 0L;
            BigDecimal totalAmount = BigDecimal.ZERO;
            List<WarehouseActivityItemResponse> items = new ArrayList<>();

            for (GoodsReceiptDetail detail : details) {
                long qty = detail.getImport_quantity() != null ? detail.getImport_quantity().longValue() : 0L;
                BigDecimal price = detail.getImport_price() != null ? detail.getImport_price() : BigDecimal.ZERO;
                BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(qty));

                totalQuantity += qty;
                totalAmount = totalAmount.add(lineTotal);

                items.add(WarehouseActivityItemResponse.builder()
                        .detailId(detail.getReceiptDetailId())
                        .productId(detail.getProduct() != null ? detail.getProduct().getProductId() : null)
                        .productName(detail.getProduct() != null ? detail.getProduct().getName() : null)
                        .locationId(detail.getLocation() != null ? detail.getLocation().getLocationId() : null)
                        .locationName(detail.getLocation() != null ? detail.getLocation().getLocationName() : null)
                        .quantity(qty)
                        .unitPrice(price)
                        .totalPrice(lineTotal)
                        .build());
            }

            return WarehouseActivityDetailResponse.builder()
                    .activityType("IMPORT")
                    .activityId(importNote.getReceiptId())
                    .activityCode("PN-" + importNote.getReceiptId())
                    .timestamp(importNote.getImport_date())
                    .description(importNote.getSupplier())
                    .warehouseId(importNote.getWarehouse() != null ? importNote.getWarehouse().getWarehouseId() : null)
                    .warehouseName(importNote.getWarehouse() != null ? importNote.getWarehouse().getWarehouseName() : null)
                    .employeeId(importNote.getEmployee() != null ? importNote.getEmployee().getEmployeeId() : null)
                    .employeeName(importNote.getEmployee() != null ? importNote.getEmployee().getFullname() : null)
                    .totalQuantity(totalQuantity)
                    .totalAmount(totalAmount)
                    .totalProductTypes(items.size())
                    .items(items)
                    .build();

        } else if (type.equalsIgnoreCase("EXPORT")) {
            ExportNote exportNote = exportNoteRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.EXPORT_NOTE_NOT_EXISTED));

            List<GoodsDeliveryDetail> details = goodsDeliveryDetailRepository.findByExportNote_DeliveryId(id);
            long totalQuantity = 0L;
            BigDecimal totalAmount = BigDecimal.ZERO;
            List<WarehouseActivityItemResponse> items = new ArrayList<>();

            for (GoodsDeliveryDetail detail : details) {
                long qty = detail.getExport_quantity() != null ? detail.getExport_quantity().longValue() : 0L;
                BigDecimal price = detail.getExport_price() != null ? detail.getExport_price() : BigDecimal.ZERO;
                BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(qty));

                totalQuantity += qty;
                totalAmount = totalAmount.add(lineTotal);

                items.add(WarehouseActivityItemResponse.builder()
                        .detailId(detail.getDeliveryDetailId())
                        .productId(detail.getProduct() != null ? detail.getProduct().getProductId() : null)
                        .productName(detail.getProduct() != null ? detail.getProduct().getName() : null)
                        .locationId(detail.getLocation() != null ? detail.getLocation().getLocationId() : null)
                        .locationName(detail.getLocation() != null ? detail.getLocation().getLocationName() : null)
                        .quantity(qty)
                        .unitPrice(price)
                        .totalPrice(lineTotal)
                        .build());
            }

            return WarehouseActivityDetailResponse.builder()
                    .activityType("EXPORT")
                    .activityId(exportNote.getDeliveryId())
                    .activityCode("PX-" + exportNote.getDeliveryId())
                    .timestamp(exportNote.getExport_date())
                    .description(exportNote.getPurpose())
                    .warehouseId(exportNote.getWarehouse() != null ? exportNote.getWarehouse().getWarehouseId() : null)
                    .warehouseName(exportNote.getWarehouse() != null ? exportNote.getWarehouse().getWarehouseName() : null)
                    .employeeId(exportNote.getEmployee() != null ? exportNote.getEmployee().getEmployeeId() : null)
                    .employeeName(exportNote.getEmployee() != null ? exportNote.getEmployee().getFullname() : null)
                    .totalQuantity(totalQuantity)
                    .totalAmount(totalAmount)
                    .totalProductTypes(items.size())
                    .items(items)
                    .build();

        } else {
            throw new AppException(ErrorCode.INVALID_ACTIVITY_TYPE);
        }
    }
}
