package com.example.e_commerce.service;

import com.example.e_commerce.dto.request.discount.DiscountRequest;
import com.example.e_commerce.dto.response.discount.DiscountResponse;
import com.example.e_commerce.dto.response.discount.PromotionLogResponse;
import com.example.e_commerce.dto.response.product.ProductDiscountResponse;
import com.example.e_commerce.entity.Discount;
import com.example.e_commerce.entity.Employee;
import com.example.e_commerce.entity.Product;
import com.example.e_commerce.entity.ProductDiscount;
import com.example.e_commerce.entity.PromotionLog;
import com.example.e_commerce.enums.ErrorCode;
import com.example.e_commerce.exception.AppException;
import com.example.e_commerce.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DiscountService {

    ProductDiscountRepository productDiscountRepository;
    ProductService productService;
    DiscountRepository discountRepository;
    PromotionLogRepository promotionLogRepository;
    EmployeeRepository employeeRepository;
    ProductRepository productRepository;
    OrderRepository orderRepository;

    public ProductDiscountResponse productDiscount(Long productId) {
        BigDecimal originalPrice = productService.getProductById(productId).getUnitPrice();

        List<ProductDiscount> productDiscounts = productDiscountRepository.findByProduct_ProductIdWithDiscount(productId);

        if (productDiscounts.isEmpty()) {
            return buildDefaultResponse(productId, originalPrice);
        }

        ProductDiscount bestProductDiscount = getBestProductDiscount(productDiscounts, originalPrice);

        if (bestProductDiscount == null) {
            return buildDefaultResponse(productId, originalPrice);
        }

        BigDecimal discountedPrice = calculateDiscountedPrice(originalPrice, bestProductDiscount.getDiscount());

        return ProductDiscountResponse.builder()
                .productId(productId)
                .discountedPrice(discountedPrice)
                .applicableQuantity(bestProductDiscount.getApplicableQuantity())
                .isActive(true)
                .build();
    }

    public Map<Long, ProductDiscountResponse> getDiscountMap(Map<Long, BigDecimal> productPriceMap) {
        if (productPriceMap == null || productPriceMap.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Long> productIds = new ArrayList<>(productPriceMap.keySet());
        LocalDateTime now = LocalDateTime.now();

        List<ProductDiscount> activeDiscounts = productDiscountRepository
                .findAllActiveDiscountsByProductIds(productIds, now);

        Map<Long, List<ProductDiscount>> discountGroupedByProduct = activeDiscounts.stream()
                .collect(Collectors.groupingBy(pd -> pd.getProduct().getProductId()));

        Map<Long, ProductDiscountResponse> resultMap = new HashMap<>();

        for (Map.Entry<Long, BigDecimal> entry : productPriceMap.entrySet()) {
            Long productId = entry.getKey();
            BigDecimal originalPrice = entry.getValue();

            List<ProductDiscount> pDiscounts = discountGroupedByProduct.getOrDefault(productId, Collections.emptyList());

            ProductDiscount bestDiscount = getBestProductDiscount(pDiscounts, originalPrice);

            if (bestDiscount == null) {
                resultMap.put(productId, buildDefaultResponse(productId, originalPrice));
            } else {
                BigDecimal discountedPrice = calculateDiscountedPrice(originalPrice, bestDiscount.getDiscount());

                resultMap.put(productId, ProductDiscountResponse.builder()
                        .productId(productId)
                        .discountId(bestDiscount.getDiscount().getDiscountId())
                        .discountedPrice(discountedPrice)
                        .applicableQuantity(bestDiscount.getApplicableQuantity())
                        .isActive(true)
                        .build());
            }
        }

        return resultMap;
    }

    @Transactional
    public void deductDiscountQuantity(Long productId, Long discountId, Long quantity) {
        int updatedRows = productDiscountRepository.decreaseDiscountQuantity(productId, discountId, quantity);

        if (updatedRows == 0) {
            throw new AppException(ErrorCode.DISCOUNT_OUT_OF_STOCK);
        }
    }

    private ProductDiscount getBestProductDiscount(List<ProductDiscount> productDiscounts, BigDecimal originalPrice) {
        LocalDateTime now = LocalDateTime.now();
        ProductDiscount bestProductDiscount = null;
        BigDecimal maxDiscountAmount = BigDecimal.ZERO;

        for (ProductDiscount pd : productDiscounts) {
            if (pd.getApplicableQuantity() != null && pd.getApplicableQuantity() <= 0) {
                continue;
            }

            Discount discount = pd.getDiscount();

            if (discount != null
                    && discount.getStartDate() != null
                    && discount.getEndDate() != null
                    && !now.isBefore(discount.getStartDate())
                    && !now.isAfter(discount.getEndDate())) {

                BigDecimal currentDiscountAmount = getCalculatedDiscountAmount(originalPrice, discount);

                if (currentDiscountAmount.compareTo(maxDiscountAmount) > 0) {
                    maxDiscountAmount = currentDiscountAmount;
                    bestProductDiscount = pd;
                }
            }
        }

        return bestProductDiscount;
    }

    private BigDecimal getCalculatedDiscountAmount(BigDecimal originalPrice, Discount discount) {
        if (discount != null && discount.getDiscountAmount() != null) {
            return discount.getDiscountAmount();
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal calculateDiscountedPrice(BigDecimal originalPrice, Discount discount) {
        BigDecimal discountAmount = getCalculatedDiscountAmount(originalPrice, discount);
        BigDecimal discountedPrice = originalPrice.subtract(discountAmount);

        return discountedPrice.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : discountedPrice;
    }

    private ProductDiscountResponse buildDefaultResponse(Long productId, BigDecimal originalPrice) {
        return ProductDiscountResponse.builder()
                .productId(productId)
                .discountedPrice(originalPrice)
                .applicableQuantity(null)
                .isActive(false)
                .build();
    }


    public Discount findDiscountById(Long id){
        return discountRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.DISCOUNT_NOT_EXISTED));
    }

    public void updateApplicableDiscount(Long productId , Long discountId , Long newQuantity){
        ProductDiscount pd = productDiscountRepository.findByProductIdAndDiscountId(productId , discountId);
        pd.setApplicableQuantity(newQuantity);
        productDiscountRepository.save(pd);
    }

    public DiscountResponse mapToDiscountResponse(Discount discount) {
        LocalDateTime now = LocalDateTime.now();
        boolean isActive = false;
        String status = "DA_HET_HAN";

        if (discount.getStartDate() != null && discount.getEndDate() != null) {
            if (now.isBefore(discount.getStartDate())) {
                status = "SAP_DIEN_RA";
            } else if (!now.isAfter(discount.getEndDate())) {
                isActive = true;
                status = "HOAT_DONG";
            }
        }

        List<Long> productIds = Collections.emptyList();
        int totalProducts = 0;
        if (discount.getProductDiscounts() != null) {
            totalProducts = discount.getProductDiscounts().size();
            productIds = discount.getProductDiscounts().stream()
                    .map(pd -> pd.getProduct() != null ? pd.getProduct().getProductId() : null)
                    .filter(Objects::nonNull)
                    .toList();
        }

        return DiscountResponse.builder()
                .discountId(discount.getDiscountId())
                .description(discount.getDescription())
                .discountAmount(discount.getDiscountAmount())
                .startDate(discount.getStartDate())
                .endDate(discount.getEndDate())
                .isActive(isActive)
                .status(status)
                .totalApplicableProducts(totalProducts)
                .applicableProductIds(productIds)
                .build();
    }

    @Transactional(readOnly = true)
    public Page<DiscountResponse> getAllDiscounts(Boolean activeOnly, Pageable pageable) {
        if (Boolean.TRUE.equals(activeOnly)) {
            return discountRepository.findActiveDiscounts(LocalDateTime.now(), pageable)
                    .map(this::mapToDiscountResponse);
        }
        return discountRepository.findAll(pageable)
                .map(this::mapToDiscountResponse);
    }

    @Transactional(readOnly = true)
    public DiscountResponse getDiscountDetail(Long discountId) {
        Discount discount = findDiscountById(discountId);
        return mapToDiscountResponse(discount);
    }

    @Transactional
    public DiscountResponse createDiscount(DiscountRequest request, String employeeId) {
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new AppException(ErrorCode.INVALID_DISCOUNT_DATE);
        }

        Discount discount = Discount.builder()
                .description(request.getDescription())
                .discountAmount(request.getDiscountAmount())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .productDiscounts(new HashSet<>())
                .build();

        Discount savedDiscount = discountRepository.save(discount);

        if (request.getProductIds() != null && !request.getProductIds().isEmpty()) {
            Long applicableQty = request.getApplicableQuantity() != null ? request.getApplicableQuantity() : 100L;
            for (Long productId : request.getProductIds()) {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

                ProductDiscount pd = ProductDiscount.builder()
                        .discount(savedDiscount)
                        .product(product)
                        .applicableQuantity(applicableQty)
                        .build();

                productDiscountRepository.save(pd);
                savedDiscount.addProductDiscount(pd);
            }
        }

        Employee employee = null;
        if (employeeId != null && !employeeId.isBlank()) {
            employee = employeeRepository.findById(employeeId).orElse(null);
        }

        PromotionLog log = PromotionLog.builder()
                .action("CREATE: Tạo mã giảm giá [" + savedDiscount.getDescription() + "] trị giá " + savedDiscount.getDiscountAmount())
                .modifiedAt(LocalDateTime.now())
                .discount(savedDiscount)
                .employee(employee)
                .build();
        promotionLogRepository.save(log);

        return mapToDiscountResponse(savedDiscount);
    }

    @Transactional
    public DiscountResponse updateDiscount(Long discountId, DiscountRequest request, String employeeId) {
        Discount discount = findDiscountById(discountId);

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new AppException(ErrorCode.INVALID_DISCOUNT_DATE);
        }

        discount.setDescription(request.getDescription());
        discount.setDiscountAmount(request.getDiscountAmount());
        discount.setStartDate(request.getStartDate());
        discount.setEndDate(request.getEndDate());

        if (request.getProductIds() != null) {
            discount.getProductDiscounts().clear();
            productDiscountRepository.flush();

            Long applicableQty = request.getApplicableQuantity() != null ? request.getApplicableQuantity() : 100L;
            for (Long productId : request.getProductIds()) {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

                ProductDiscount pd = ProductDiscount.builder()
                        .discount(discount)
                        .product(product)
                        .applicableQuantity(applicableQty)
                        .build();

                productDiscountRepository.save(pd);
                discount.addProductDiscount(pd);
            }
        }

        Discount savedDiscount = discountRepository.save(discount);

        Employee employee = null;
        if (employeeId != null && !employeeId.isBlank()) {
            employee = employeeRepository.findById(employeeId).orElse(null);
        }

        PromotionLog log = PromotionLog.builder()
                .action("UPDATE: Cập nhật mã giảm giá ID " + discountId + " [" + savedDiscount.getDescription() + "] trị giá " + savedDiscount.getDiscountAmount())
                .modifiedAt(LocalDateTime.now())
                .discount(savedDiscount)
                .employee(employee)
                .build();
        promotionLogRepository.save(log);

        return mapToDiscountResponse(savedDiscount);
    }

    @Transactional
    public void deleteDiscount(Long discountId, String employeeId) {
        Discount discount = findDiscountById(discountId);

        if (orderRepository.existsByDiscount_DiscountId(discountId)) {
            throw new AppException(ErrorCode.DISCOUNT_IN_USE);
        }

        Employee employee = null;
        if (employeeId != null && !employeeId.isBlank()) {
            employee = employeeRepository.findById(employeeId).orElse(null);
        }

        PromotionLog log = PromotionLog.builder()
                .action("DELETE: Xóa mã giảm giá ID " + discountId + " [" + discount.getDescription() + "] trị giá " + discount.getDiscountAmount())
                .modifiedAt(LocalDateTime.now())
                .discount(null)
                .employee(employee)
                .build();
        promotionLogRepository.save(log);

        discountRepository.delete(discount);
    }

    @Transactional(readOnly = true)
    public Page<PromotionLogResponse> getPromotionLogs(Long discountId, Pageable pageable) {
        Page<PromotionLog> logsPage;
        if (discountId != null) {
            logsPage = promotionLogRepository.findByDiscount_DiscountIdOrderByModifiedAtDesc(discountId, pageable);
        } else {
            logsPage = promotionLogRepository.findAllByOrderByModifiedAtDesc(pageable);
        }

        return logsPage.map(pl -> PromotionLogResponse.builder()
                .promLogId(pl.getPromLogId())
                .action(pl.getAction())
                .modifiedAt(pl.getModifiedAt())
                .discountId(pl.getDiscount() != null ? pl.getDiscount().getDiscountId() : null)
                .discountCode(pl.getDiscount() != null ? pl.getDiscount().getDescription() : null)
                .employeeId(pl.getEmployee() != null ? pl.getEmployee().getEmployeeId() : null)
                .employeeName(pl.getEmployee() != null ? pl.getEmployee().getFullname() : null)
                .build());
    }

}