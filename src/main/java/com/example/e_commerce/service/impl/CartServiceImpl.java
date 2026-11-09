package com.example.e_commerce.service.impl;



import com.example.e_commerce.dto.request.cart.AddToCartRequest;
import com.example.e_commerce.dto.response.product.ProductDiscountResponse;
import com.example.e_commerce.dto.response.cart.CartItemResponse;
import com.example.e_commerce.dto.response.cart.CartResponse;
import com.example.e_commerce.dto.response.cart.CartSummary;
import com.example.e_commerce.dto.response.cart.InvalidCartItemResponse;
import com.example.e_commerce.entity.Cart;
import com.example.e_commerce.entity.CartDetail;
import com.example.e_commerce.entity.Product;
import com.example.e_commerce.enums.ErrorCode;
import com.example.e_commerce.exception.AppException;
import com.example.e_commerce.exception.CartValidationException;
import com.example.e_commerce.repository.CartDetailRepository;
import com.example.e_commerce.repository.CartRepository;
import com.example.e_commerce.service.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
public class CartServiceImpl implements CartService {

    ProductService productService;
    CartRepository cartRepository;
    CartDetailRepository cartDetailRepository;
    DiscountService discountService;
    LocationService locationService;
    ProductImageService productImageService;

    @Override
    public void addToCart(String customerId, AddToCartRequest request) {
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new AppException(ErrorCode.INVALID_QUANTITY);
        }

        Product product = productService.getProductById(request.getProductId());
        Cart cart = cartRepository.findByCustomer_CustomerId(customerId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTED));

        Optional<CartDetail> existedCartDetail = cartDetailRepository
                .findByCart_CartIdAndProduct_ProductId(cart.getCartId(), request.getProductId());

        Long currentQuantityInCart = existedCartDetail.map(CartDetail::getQuantity).orElse(0L);
        Long totalRequestedQuantity = currentQuantityInCart + request.getQuantity();

        Long stockQuantity = locationService.countTotalStockByProductId(request.getProductId());

        if (stockQuantity < totalRequestedQuantity) {
            throw new AppException(ErrorCode.OUT_OF_STOCK);
        }

        if (existedCartDetail.isPresent()) {
            CartDetail cartDetail = existedCartDetail.get();
            Long newQuantity = cartDetail.getQuantity() + request.getQuantity();

            cartDetail.setQuantity(newQuantity);

        } else {
            CartDetail cartDetail = new CartDetail();
            cartDetail.setQuantity(request.getQuantity());
            cartDetail.setProduct(product);

            cart.addCartDetail(cartDetail);

            cartRepository.save(cart);

        }
    }


    @Override
    @Transactional(readOnly = true)
    public CartResponse getCartByCustomerId(String customerId) {
        Cart cart = cartRepository.findByCustomer_CustomerId(customerId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTED));

        List<CartDetail> cartDetailList = cartDetailRepository.findByCart_CartId(cart.getCartId());

        if (cartDetailList.isEmpty()) {
            return CartResponse.builder()
                    .cartId(cart.getCartId())
                    .customerId(customerId)
                    .items(Collections.emptyList())
                    .summary(CartSummary.builder()
                            .totalOriginalPrice(BigDecimal.ZERO)
                            .totalDiscountAmount(BigDecimal.ZERO)
                            .totalPayment(BigDecimal.ZERO)
                            .build())
                    .build();
        }

        List<Long> productIds = cartDetailList.stream()
                .map(cartDetail -> cartDetail.getProduct().getProductId())
                .distinct()
                .toList();

        Map<Long, BigDecimal> productPriceMap = productService.productPriceMap(productIds);
        Map<Long, ProductDiscountResponse> discountMap = discountService.getDiscountMap(productPriceMap);
        Map<Long, Long> stockMap = locationService.getStockMap(productIds);
        Map<Long, String> imageMap = productImageService.imageMap(productIds);

        BigDecimal totalOriginalPrice = BigDecimal.ZERO;
        BigDecimal totalPayment = BigDecimal.ZERO;

        List<CartItemResponse> itemResponses = new ArrayList<>();

        for (CartDetail detail : cartDetailList) {
            Product product = detail.getProduct();
            Long productId = product.getProductId();
            Long quantity = detail.getQuantity() != null ? detail.getQuantity() : 0L;

            BigDecimal originalPrice = productPriceMap.getOrDefault(productId, BigDecimal.ZERO);
            ProductDiscountResponse discountInfo = discountMap.get(productId);

            BigDecimal discountedPrice = (discountInfo != null && discountInfo.getDiscountedPrice() != null)
                    ? discountInfo.getDiscountedPrice()
                    : originalPrice;

            Long applicableQuantity = (discountInfo != null) ? discountInfo.getApplicableQuantity() : null;

            BigDecimal subTotal;
            if (discountInfo != null && discountInfo.getIsActive()
                    && applicableQuantity != null && applicableQuantity > 0
                    && discountedPrice.compareTo(originalPrice) < 0) {

                if (quantity <= applicableQuantity) {
                    subTotal = discountedPrice.multiply(BigDecimal.valueOf(quantity));
                } else {
                    long discountedQty = applicableQuantity;
                    long fullPriceQty = quantity - applicableQuantity;

                    BigDecimal discountedAmount = discountedPrice.multiply(BigDecimal.valueOf(discountedQty));
                    BigDecimal fullPriceAmount = originalPrice.multiply(BigDecimal.valueOf(fullPriceQty));

                    subTotal = discountedAmount.add(fullPriceAmount);
                }
            } else {
                subTotal = discountedPrice.multiply(BigDecimal.valueOf(quantity));
            }

            BigDecimal itemOriginalSubTotal = originalPrice.multiply(BigDecimal.valueOf(quantity));
            totalOriginalPrice = totalOriginalPrice.add(itemOriginalSubTotal);
            totalPayment = totalPayment.add(subTotal);

            Long stockAvailable = stockMap.getOrDefault(productId, 0L);
            Boolean isAvailable = stockAvailable >= quantity;
            String imageUrl = imageMap.get(productId);

            CartItemResponse itemResponse = CartItemResponse.builder()
                    .productId(productId)
                    .productName(product.getName())
                    .imageUrl(imageUrl)
                    .originalPrice(originalPrice)
                    .discountedPrice(discountedPrice)
                    .quantity(quantity)
                    .subTotal(subTotal)
                    .stockAvailable(stockAvailable)
                    .isAvailable(isAvailable)
                    .build();

            itemResponses.add(itemResponse);
        }

        BigDecimal totalDiscountAmount = totalOriginalPrice.subtract(totalPayment);

        CartSummary summary = CartSummary.builder()
                .totalOriginalPrice(totalOriginalPrice)
                .totalDiscountAmount(totalDiscountAmount)
                .totalPayment(totalPayment)
                .build();

        return CartResponse.builder()
                .cartId(cart.getCartId())
                .customerId(customerId)
                .items(itemResponses)
                .summary(summary)
                .build();
    }


    @Override
    @Transactional
    public void updateCartItemQuantity(String customerId, Long productId, Long newQuantity) {
        if (newQuantity == null) {
            throw new AppException(ErrorCode.INVALID_QUANTITY);
        }

        Cart cart = cartRepository.findByCustomer_CustomerId(customerId).orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTED));
        CartDetail cartDetail = cartDetailRepository
                .findByCart_CartIdAndProduct_ProductId(cart.getCartId(), productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_IN_CART));

        if (newQuantity <= 0) {
            cartDetailRepository.delete(cartDetail);
            return;
        }

        Long stockQuantity = locationService.countTotalStockByProductId(productId);

        if (stockQuantity < newQuantity) {
            throw new AppException(ErrorCode.OUT_OF_STOCK);
        }


        cartDetail.setQuantity(newQuantity);
        cartDetailRepository.save(cartDetail);

    }


    @Override
    public void removeFromCart(String customerId, Long productId) {
        Cart cart = cartRepository.findByCustomer_CustomerId(customerId).orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTED));
        CartDetail cartDetail = cartDetailRepository
                .findByCart_CartIdAndProduct_ProductId(cart.getCartId(), productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_IN_CART));

        cartDetailRepository.delete(cartDetail);
    }

    @Override
    @Transactional
    public void clearCart(String customerId) {
        Cart cart = cartRepository.findByCustomer_CustomerId(customerId).orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTED));
        cartDetailRepository.deleteAllByCartId(cart.getCartId());
    }


    @Override
    public void validateCartStock(String customerId) {
        Cart cart = cartRepository.findByCustomer_CustomerId(customerId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTED));

        List<CartDetail> cartDetailList = cartDetailRepository.findByCart_CartId(cart.getCartId());

        if (cartDetailList.isEmpty()) {
            throw new AppException(ErrorCode.CART_IS_EMPTY);
        }

        List<InvalidCartItemResponse> invalidItems = new ArrayList<>();

        List<Long> productIds = cartDetailList.stream()
                .map(item -> item.getProduct().getProductId())
                .distinct()
                .toList();
        Map<Long, Long> stockMap = locationService.getStockMap(productIds);

        for (CartDetail item : cartDetailList) {
            Long requestedQuantity = item.getQuantity() != null ? item.getQuantity() : 0L;
            Product product = item.getProduct();
            Long stockQuantity = stockMap.getOrDefault(product.getProductId(), 0L);

            if (stockQuantity < requestedQuantity) {
                invalidItems.add(new InvalidCartItemResponse(
                        product.getProductId(),
                        product.getName(),
                        requestedQuantity,
                        stockQuantity
                ));
            }
        }

        if (!invalidItems.isEmpty()) {
            throw new CartValidationException(ErrorCode.OUT_OF_STOCK, invalidItems);
        }
    }


    @Override
    public Map<Long,Long> getListProductsInCartByCustomerIdAndProductIds(List<Long> productIds ,String customerId){
        List<CartDetailRepository.CartItemProjection> itemList = cartDetailRepository.findCartItemsByCustomerIdAndProductIds(productIds,customerId);

        if (itemList.isEmpty()) {
            throw new AppException(ErrorCode.CART_NOT_EXISTED);
        }

        return itemList.stream()
                .collect(Collectors.toMap(
                        CartDetailRepository.CartItemProjection::getProductId,
                        CartDetailRepository.CartItemProjection::getQuantity
                ));
    }

    @Override
    public List<Long> getProductIdsInCart(String customerId){
        return cartDetailRepository.findProductIdsByCustomerId(customerId);
    }

    @Override
    public List<CartDetail> getCartDetailsByCustomerId(String customerId){
        Cart cart = cartRepository.findByCustomer_CustomerId(customerId)
                .orElseThrow(()->new AppException(ErrorCode.CART_NOT_EXISTED));
        return cartDetailRepository.findByCart_CartId(cart.getCartId());
    }


}





