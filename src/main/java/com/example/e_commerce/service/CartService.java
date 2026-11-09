package com.example.e_commerce.service;

import com.example.e_commerce.dto.request.cart.AddToCartRequest;
import com.example.e_commerce.dto.response.cart.CartResponse;
import com.example.e_commerce.entity.CartDetail;

import java.util.List;
import java.util.Map;

public interface CartService {
    CartResponse getCartByCustomerId(String customerId);

    void addToCart(String customerId, AddToCartRequest request);

    void updateCartItemQuantity(String customerId, Long productId, Long newQuantity);

    void removeFromCart(String customerId, Long productId);

    void clearCart(String customerId);

    void validateCartStock(String customerId);

    Map<Long,Long> getListProductsInCartByCustomerIdAndProductIds(List<Long> productIds ,String customerId);
    List<Long> getProductIdsInCart(String customerId);
    List<CartDetail> getCartDetailsByCustomerId(String customerId);
}
