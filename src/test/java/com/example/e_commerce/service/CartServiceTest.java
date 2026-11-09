package com.example.e_commerce.service;

import com.example.e_commerce.dto.request.cart.AddToCartRequest;
import com.example.e_commerce.entity.Cart;
import com.example.e_commerce.entity.CartDetail;
import com.example.e_commerce.entity.Product;
import com.example.e_commerce.enums.ErrorCode;
import com.example.e_commerce.exception.AppException;
import com.example.e_commerce.repository.CartDetailRepository;
import com.example.e_commerce.repository.CartRepository;
import com.example.e_commerce.service.impl.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private ProductService productService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartDetailRepository cartDetailRepository;

    @Mock
    private DiscountService discountService;

    @Mock
    private LocationService locationService;

    @Mock
    private ProductImageService productImageService;

    @InjectMocks
    private CartServiceImpl cartService;

    private Cart testCart;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testCart = new Cart();
        testCart.setCartId(1L);

        testProduct = Product.builder()
                .productId(100L)
                .name("Sản phẩm mẫu")
                .unitPrice(new BigDecimal("150000"))
                .build();
    }

    @Test
    @DisplayName("Thêm vào giỏ hàng: Lỗi khi số lượng <= 0")
    void addToCart_InvalidQuantity_ThrowsException() {
        AddToCartRequest request = AddToCartRequest.builder()
                .productId(100L)
                .quantity(0L)
                .build();

        AppException exception = assertThrows(AppException.class,
                () -> cartService.addToCart("customer-1", request));

        assertEquals(ErrorCode.INVALID_QUANTITY, exception.getErrorCode());
    }

    @Test
    @DisplayName("Thêm vào giỏ hàng: Lỗi khi số lượng vượt quá tồn kho")
    void addToCart_InsufficientStock_ThrowsException() {
        AddToCartRequest request = AddToCartRequest.builder()
                .productId(100L)
                .quantity(10L)
                .build();

        when(productService.getProductById(100L)).thenReturn(testProduct);
        when(cartRepository.findByCustomer_CustomerId("customer-1")).thenReturn(Optional.of(testCart));
        when(cartDetailRepository.findByCart_CartIdAndProduct_ProductId(1L, 100L)).thenReturn(Optional.empty());
        when(locationService.countTotalStockByProductId(100L)).thenReturn(5L);

        AppException exception = assertThrows(AppException.class,
                () -> cartService.addToCart("customer-1", request));

        assertEquals(ErrorCode.OUT_OF_STOCK, exception.getErrorCode());
    }

    @Test
    @DisplayName("Thêm vào giỏ hàng thành công khi hàng còn đủ tồn kho")
    void addToCart_Success() {
        AddToCartRequest request = AddToCartRequest.builder()
                .productId(100L)
                .quantity(3L)
                .build();

        when(productService.getProductById(100L)).thenReturn(testProduct);
        when(cartRepository.findByCustomer_CustomerId("customer-1")).thenReturn(Optional.of(testCart));
        when(cartDetailRepository.findByCart_CartIdAndProduct_ProductId(1L, 100L)).thenReturn(Optional.empty());
        when(locationService.countTotalStockByProductId(100L)).thenReturn(20L);

        assertDoesNotThrow(() -> cartService.addToCart("customer-1", request));
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    @DisplayName("Cập nhật số lượng sản phẩm giỏ hàng: Xóa món nếu newQuantity <= 0")
    void updateCartItemQuantity_ZeroOrNegative_DeletesItem() {
        CartDetail cartDetail = new CartDetail();
        cartDetail.setCartDetailId(10L);
        cartDetail.setProduct(testProduct);
        cartDetail.setCart(testCart);

        when(cartRepository.findByCustomer_CustomerId("customer-1")).thenReturn(Optional.of(testCart));
        when(cartDetailRepository.findByCart_CartIdAndProduct_ProductId(1L, 100L)).thenReturn(Optional.of(cartDetail));

        cartService.updateCartItemQuantity("customer-1", 100L, 0L);

        verify(cartDetailRepository, times(1)).delete(cartDetail);
    }
}
