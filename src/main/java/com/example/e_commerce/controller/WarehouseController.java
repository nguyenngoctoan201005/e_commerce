package com.example.e_commerce.controller;

import com.example.e_commerce.dto.request.ApiResponse;
import com.example.e_commerce.dto.request.order.UpdateShippingCodeRequest;
import com.example.e_commerce.dto.request.warehouse.CreateExportNoteRequest;
import com.example.e_commerce.dto.request.warehouse.CreateImportNoteRequest;
import com.example.e_commerce.dto.request.warehouse.MoveProductRequest;
import com.example.e_commerce.dto.request.warehouse.WarehouseCreateRequest;
import com.example.e_commerce.dto.request.warehouse.WarehouseUpdateRequest;
import com.example.e_commerce.dto.response.order.OrderResponse;
import com.example.e_commerce.dto.response.warehouse.*;
import com.example.e_commerce.entity.Employee;
import com.example.e_commerce.entity.Warehouse;
import com.example.e_commerce.enums.ErrorCode;
import com.example.e_commerce.enums.OrderStatus;
import com.example.e_commerce.enums.WarehouseStatus;
import com.example.e_commerce.exception.AppException;
import com.example.e_commerce.service.EmployeeService;
import com.example.e_commerce.service.OrderService;
import com.example.e_commerce.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/warehouses")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WarehouseController {
        private final WarehouseService warehouseService;
        private final EmployeeService employeeService;
        private final OrderService orderService;

        @GetMapping
        @PreAuthorize("hasAuthority('WAREHOUSE_VIEW') or hasRole('ADMIN')")
        public ApiResponse<Page<WarehouseSummaryResponse>> getWarehouses(
                        @PageableDefault(size = 10, sort = "warehouseId", direction = Sort.Direction.DESC) Pageable pageable) {
                Page<WarehouseSummaryResponse> result = warehouseService.getWarehouses(pageable);

                return ApiResponse.<Page<WarehouseSummaryResponse>>builder()
                                .result(result)
                                .build();
        }

        @GetMapping("/{warehouseId}")
        @PreAuthorize("hasAuthority('WAREHOUSE_VIEW') or hasAuthority('INVENTORY_VIEW') or hasRole('ADMIN')")
        public ApiResponse<Page<WarehouseItemResponse>> getItemsOfWarehouse(
                        @PathVariable Long warehouseId,
                        @PageableDefault(size = 10, sort = "locationDetailId", direction = Sort.Direction.DESC) Pageable pageable) {
                Page<WarehouseItemResponse> result = warehouseService.getWarehouseItems(warehouseId, pageable);

                return ApiResponse.<Page<WarehouseItemResponse>>builder()
                                .result(result)
                                .build();
        }

        @PostMapping
        @PreAuthorize("hasAuthority('WAREHOUSE_CREATE') or hasRole('ADMIN')")
        public ApiResponse<WarehouseSummaryResponse> createWarehouse(
                        @Valid @RequestBody WarehouseCreateRequest request) {
                WarehouseSummaryResponse result = warehouseService.createWarehouse(request);
                return ApiResponse.<WarehouseSummaryResponse>builder()
                                .result(result)
                                .build();
        }

        @PutMapping("/{warehouseId}")
        @PreAuthorize("hasAuthority('WAREHOUSE_UPDATE') or hasRole('ADMIN')")
        public ApiResponse<WarehouseSummaryResponse> updateWarehouse(@PathVariable Long warehouseId,
                        @Valid @RequestBody WarehouseUpdateRequest request) {
                WarehouseSummaryResponse result = warehouseService.updateWarehouse(warehouseId, request);
                return ApiResponse.<WarehouseSummaryResponse>builder()
                                .result(result)
                                .build();
        }

        @PutMapping("/status/{warehouseId}")
        @PreAuthorize("hasAuthority('WAREHOUSE_UPDATE') or hasRole('ADMIN')")
        public ApiResponse<WarehouseSummaryResponse> updateStatusOfWarehouse(@PathVariable Long warehouseId,
                        @RequestBody WarehouseStatus status) {
                WarehouseSummaryResponse result = warehouseService.updateStatusWarehouse(warehouseId, status);
                return ApiResponse.<WarehouseSummaryResponse>builder()
                                .result(result)
                                .build();
        }

        @GetMapping("/employee/product/{warehouseId}")
        @PreAuthorize("hasAuthority('INVENTORY_VIEW') or hasAuthority('WAREHOUSE_VIEW') or hasRole('WAREHOUSE') or hasRole('ADMIN')")
        public ApiResponse<List<ProductInWarehouseResponse>> getProductsInWarehouse(@PathVariable Long warehouseId,
                        Authentication authentication) {
                String username = authentication.getName();
                Employee employee = employeeService.findByUsername(username);

                return ApiResponse.<List<ProductInWarehouseResponse>>builder()
                                .result(warehouseService.getProductsInWarehouse(warehouseId, employee.getEmployeeId()))
                                .build();
        }

        @GetMapping("/employee/product/detail/{productId}")
        @PreAuthorize("hasAuthority('INVENTORY_VIEW') or hasAuthority('WAREHOUSE_VIEW') or hasRole('WAREHOUSE') or hasRole('ADMIN')")
        public ApiResponse<ProductDetailInWarehouseResponse> getProductDetailInWarehouse(@PathVariable Long productId,
                        Authentication authentication) {
                String username = authentication.getName();
                Employee employee = employeeService.findByUsername(username);
                Warehouse warehouse = warehouseService.findByEmployeeId(employee.getEmployeeId());

                return ApiResponse.<ProductDetailInWarehouseResponse>builder()
                                .result(warehouseService.getProductDetailInWarehouse(warehouse.getWarehouseId(),
                                                productId))
                                .build();
        }

        @PutMapping("/employee/product/move/{productId}")
        @PreAuthorize("hasAuthority('INVENTORY_UPDATE') or hasAuthority('WAREHOUSE_UPDATE') or hasRole('WAREHOUSE') or hasRole('ADMIN')")
        public ApiResponse<ProductDetailInWarehouseResponse> moveProduct(
                        @PathVariable Long productId,
                        @Valid @RequestBody MoveProductRequest request,
                        Authentication authentication) {
                String username = authentication.getName();
                Employee employee = employeeService.findByUsername(username);
                Warehouse warehouse = warehouseService.findByEmployeeId(employee.getEmployeeId());

                ProductDetailInWarehouseResponse result = warehouseService.moveProduct(
                                warehouse.getWarehouseId(), productId, request);

                return ApiResponse.<ProductDetailInWarehouseResponse>builder()
                                .result(result)
                                .message("Chuyển vị trí sản phẩm trong kho thành công")
                                .build();
        }

        @PutMapping("/{warehouseId}/product/move/{productId}")
        @PreAuthorize("hasAuthority('WAREHOUSE_UPDATE') or hasAuthority('INVENTORY_UPDATE') or hasRole('ADMIN')")
        public ApiResponse<ProductDetailInWarehouseResponse> moveProductByWarehouseId(
                        @PathVariable Long warehouseId,
                        @PathVariable Long productId,
                        @Valid @RequestBody MoveProductRequest request) {
                ProductDetailInWarehouseResponse result = warehouseService.moveProduct(
                                warehouseId, productId, request);

                return ApiResponse.<ProductDetailInWarehouseResponse>builder()
                        .result(result)
                        .message("Chuyển vị trí sản phẩm trong kho thành công")
                        .build();
        }

        @PostMapping("/employee/import")
        @PreAuthorize("hasAuthority('IMPORT_NOTE_CREATE') or hasAuthority('WAREHOUSE_UPDATE') or hasRole('WAREHOUSE') or hasRole('ADMIN')")
        public ApiResponse<ImportNoteResponse> createImportNoteByEmployee(
                        @Valid @RequestBody CreateImportNoteRequest request,
                        Authentication authentication) {
                String username = authentication.getName();
                Employee employee = employeeService.findByUsername(username);
                Warehouse warehouse = warehouseService.findByEmployeeId(employee.getEmployeeId());

                ImportNoteResponse result = warehouseService.createImportNote(
                                warehouse.getWarehouseId(), employee.getEmployeeId(), request);

                return ApiResponse.<ImportNoteResponse>builder()
                                .result(result)
                                .message("Tạo phiếu nhập kho thành công")
                                .build();
        }

        @PostMapping("/{warehouseId}/import")
        @PreAuthorize("hasAuthority('IMPORT_NOTE_CREATE') or hasAuthority('WAREHOUSE_UPDATE') or hasRole('ADMIN')")
        public ApiResponse<ImportNoteResponse> createImportNote(
                        @PathVariable Long warehouseId,
                        @Valid @RequestBody CreateImportNoteRequest request,
                        Authentication authentication) {
                String username = authentication.getName();
                Employee employee = employeeService.findOptionalByUsername(username).orElse(null);
                String employeeId = employee != null ? employee.getEmployeeId() : null;

                ImportNoteResponse result = warehouseService.createImportNote(
                                warehouseId, employeeId, request);

                return ApiResponse.<ImportNoteResponse>builder()
                                .result(result)
                                .message("Tạo phiếu nhập kho thành công")
                                .build();
        }

        @GetMapping("/import-notes/{receiptId}")
        @PreAuthorize("hasAuthority('INVENTORY_VIEW') or hasAuthority('WAREHOUSE_VIEW') or hasRole('WAREHOUSE') or hasRole('ADMIN')")
        public ApiResponse<ImportNoteResponse> getImportNoteDetail(@PathVariable Long receiptId) {
                return ApiResponse.<ImportNoteResponse>builder()
                                .result(warehouseService.getImportNoteDetail(receiptId))
                                .build();
        }

        @PostMapping("/employee/export")
        @PreAuthorize("hasAuthority('EXPORT_NOTE_CREATE') or hasAuthority('WAREHOUSE_UPDATE') or hasRole('WAREHOUSE') or hasRole('ADMIN')")
        public ApiResponse<ExportNoteResponse> createExportNoteByEmployee(
                        @Valid @RequestBody CreateExportNoteRequest request,
                        Authentication authentication) {
                String username = authentication.getName();
                Employee employee = employeeService.findByUsername(username);
                Warehouse warehouse = warehouseService.findByEmployeeId(employee.getEmployeeId());

                ExportNoteResponse result = warehouseService.createExportNote(
                                warehouse.getWarehouseId(), employee.getEmployeeId(), request);

                return ApiResponse.<ExportNoteResponse>builder()
                                .result(result)
                                .message("Tạo phiếu xuất kho thành công")
                                .build();
        }

        @PostMapping("/{warehouseId}/export")
        @PreAuthorize("hasAuthority('EXPORT_NOTE_CREATE') or hasAuthority('WAREHOUSE_UPDATE') or hasRole('ADMIN')")
        public ApiResponse<ExportNoteResponse> createExportNote(
                        @PathVariable Long warehouseId,
                        @Valid @RequestBody CreateExportNoteRequest request,
                        Authentication authentication) {
                String username = authentication.getName();
                Employee employee = employeeService.findOptionalByUsername(username).orElse(null);
                String employeeId = employee != null ? employee.getEmployeeId() : null;

                ExportNoteResponse result = warehouseService.createExportNote(
                                warehouseId, employeeId, request);

                return ApiResponse.<ExportNoteResponse>builder()
                                .result(result)
                                .message("Tạo phiếu xuất kho thành công")
                                .build();
        }

        @GetMapping("/export-notes/{deliveryId}")
        @PreAuthorize("hasAuthority('INVENTORY_VIEW') or hasAuthority('WAREHOUSE_VIEW') or hasRole('WAREHOUSE') or hasRole('ADMIN')")
        public ApiResponse<ExportNoteResponse> getExportNoteDetail(@PathVariable Long deliveryId) {
                return ApiResponse.<ExportNoteResponse>builder()
                                .result(warehouseService.getExportNoteDetail(deliveryId))
                                .build();
        }

        @GetMapping("/employee/activities")
        @PreAuthorize("hasAuthority('INVENTORY_VIEW') or hasAuthority('WAREHOUSE_VIEW') or hasRole('WAREHOUSE') or hasRole('ADMIN')")
        public ApiResponse<Page<WarehouseActivityResponse>> getMyActivities(
                        @RequestParam(required = false) String type,
                        @PageableDefault(size = 10) Pageable pageable,
                        Authentication authentication) {
                String username = authentication.getName();
                Employee employee = employeeService.findByUsername(username);

                Page<WarehouseActivityResponse> result = warehouseService.getEmployeeActivities(
                                employee.getEmployeeId(), type, pageable);

                return ApiResponse.<Page<WarehouseActivityResponse>>builder()
                                .result(result)
                                .build();
        }

        @GetMapping("/employees/{employeeId}/activities")
        @PreAuthorize("hasAuthority('WAREHOUSE_VIEW') or hasRole('ADMIN')")
        public ApiResponse<Page<WarehouseActivityResponse>> getEmployeeActivities(
                        @PathVariable String employeeId,
                        @RequestParam(required = false) String type,
                        @PageableDefault(size = 10) Pageable pageable) {
                Page<WarehouseActivityResponse> result = warehouseService.getEmployeeActivities(
                                employeeId, type, pageable);

                return ApiResponse.<Page<WarehouseActivityResponse>>builder()
                                .result(result)
                                .build();
        }

        @GetMapping("/{warehouseId}/activities")
        @PreAuthorize("hasAuthority('WAREHOUSE_VIEW') or hasRole('ADMIN')")
        public ApiResponse<Page<WarehouseActivityResponse>> getWarehouseActivities(
                        @PathVariable Long warehouseId,
                        @RequestParam(required = false) String type,
                        @PageableDefault(size = 10) Pageable pageable) {
                Page<WarehouseActivityResponse> result = warehouseService.getWarehouseActivities(
                                warehouseId, type, pageable);

                return ApiResponse.<Page<WarehouseActivityResponse>>builder()
                                .result(result)
                                .build();
        }

        @GetMapping("/activities/{type}/{id}")
        @PreAuthorize("hasAuthority('INVENTORY_VIEW') or hasAuthority('WAREHOUSE_VIEW') or hasRole('WAREHOUSE') or hasRole('ADMIN')")
        public ApiResponse<WarehouseActivityDetailResponse> getActivityDetail(
                        @PathVariable String type,
                        @PathVariable Long id) {
                WarehouseActivityDetailResponse result = warehouseService.getActivityDetail(type, id);

                return ApiResponse.<WarehouseActivityDetailResponse>builder()
                                .result(result)
                                .build();
        }

        @GetMapping("/orders/managed")
        @PreAuthorize("hasAuthority('INVENTORY_VIEW') or hasAuthority('WAREHOUSE_VIEW') or hasAuthority('ORDER_VIEW_ALL') or hasRole('WAREHOUSE') or hasRole('ADMIN')")
        public ApiResponse<Page<OrderResponse>> getManagedOrders(
                        @RequestParam(required = false) OrderStatus status,
                        @PageableDefault(size = 10, sort = "orderDate", direction = Sort.Direction.DESC) Pageable pageable,
                        Authentication authentication) {
                String username = authentication.getName();
                Employee employee = employeeService.findByUsername(username);

                Page<OrderResponse> result = orderService.getOrdersByEmployeeProvinces(
                                employee.getEmployeeId(), status, pageable);

                return ApiResponse.<Page<OrderResponse>>builder()
                                .result(result)
                                .build();
        }

        @GetMapping("/orders/managed/{orderId}")
        @PreAuthorize("hasAuthority('INVENTORY_VIEW') or hasAuthority('WAREHOUSE_VIEW') or hasAuthority('ORDER_VIEW_ALL') or hasRole('WAREHOUSE') or hasRole('ADMIN')")
        public ApiResponse<OrderResponse> getManagedOrderDetail(
                        @PathVariable Long orderId,
                        Authentication authentication) {
                String username = authentication.getName();
                Employee employee = employeeService.findByUsername(username);

                OrderResponse result = orderService.getOrderDetailByEmployeeProvinces(
                                orderId, employee.getEmployeeId());

                return ApiResponse.<OrderResponse>builder()
                                .result(result)
                                .build();
        }

        @PutMapping("/orders/managed/{orderId}/shipping-code")
        @PreAuthorize("hasAuthority('INVENTORY_UPDATE') or hasAuthority('WAREHOUSE_UPDATE') or hasAuthority('ORDER_UPDATE') or hasRole('WAREHOUSE') or hasRole('ADMIN')")
        public ApiResponse<OrderResponse> updateShippingCode(
                        @PathVariable Long orderId,
                        @Valid @RequestBody UpdateShippingCodeRequest request,
                        Authentication authentication) {
                String username = authentication.getName();
                Employee employee = employeeService.findByUsername(username);

                OrderResponse result = orderService.updateShippingCode(
                                orderId, employee.getEmployeeId(), request);

                return ApiResponse.<OrderResponse>builder()
                                .result(result)
                                .message("Cập nhật mã vận chuyển thành công")
                                .build();
        }

}
