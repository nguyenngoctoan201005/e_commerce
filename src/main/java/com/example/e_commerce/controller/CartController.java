package com.example.e_commerce.controller;

import com.example.e_commerce.dto.request.ApiResponse;
import com.example.e_commerce.dto.request.cart.AddToCartRequest;
import com.example.e_commerce.dto.response.AuthenticationResponse;
import com.example.e_commerce.dto.response.cart.CartResponse;
import com.example.e_commerce.entity.Customer;
import com.example.e_commerce.service.CartService;
import com.example.e_commerce.service.impl.CartServiceImpl;
import com.example.e_commerce.service.CustomerService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {

    CartService cartService ;
    CustomerService customerService;

    @GetMapping("/my-cart")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<CartResponse> getMyCart(Authentication authentication) {
        String emailCustomer = authentication.getName();
        Customer customer = customerService.findByEmail(emailCustomer);
        return ApiResponse.<CartResponse>builder()
                .result(cartService.getCartByCustomerId(customer.getCustomerId()))
                .build();
    }

    @PostMapping("/add-product")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<Void> addToCart(@Valid @RequestBody AddToCartRequest request, Authentication authentication){
        String emailCustomer = authentication.getName();
        Customer customer = customerService.findByEmail(emailCustomer);
        String customerId = customer.getCustomerId();
        cartService.addToCart(customerId, request);

        return ApiResponse.<Void>builder()
                .message("Thêm sản phẩm vào giỏ hàng thành công")
                .build();
    }


    @GetMapping("/{customerId}")
    @PreAuthorize("hasAuthority('ORDER_VIEW_ALL') or hasRole('ADMIN')")
    public ApiResponse<CartResponse>getCartByCustomerId(@PathVariable String customerId){
        return ApiResponse.<CartResponse>builder()
                .result(cartService.getCartByCustomerId(customerId))
                .build();
    }


    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<Void>updateCartItemQuantity(@PathVariable Long productId ,@RequestParam Long quantity, Authentication authentication ){
        String emailCustomer = authentication.getName();
        Customer customer = customerService.findByEmail(emailCustomer);
        String customerId = customer.getCustomerId();

        cartService.updateCartItemQuantity(customerId,productId,quantity);
        return ApiResponse.<Void>builder()
                .message("Cập nhật sản phẩm thành công")
                .build();
    }


    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<Void> removeFromCart(Authentication authentication , @PathVariable Long productId){
        String emailCustomer = authentication.getName();
        Customer customer = customerService.findByEmail(emailCustomer);
        String customerId = customer.getCustomerId();

        cartService.removeFromCart(customerId,productId);
        return ApiResponse.<Void>builder()
                .message("Xóa sản phẩm thành công !")
                .build();
    }


    @DeleteMapping("/clear-cart")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<Void> clearCart(Authentication authentication){
        String emailCustomer = authentication.getName();
        Customer customer = customerService.findByEmail(emailCustomer);
        String customerId = customer.getCustomerId();

        cartService.clearCart(customerId);
        return ApiResponse.<Void>builder()
                .message("Xóa toàn bộ giỏ hàng thành công !")
                .build();
    }

    @PostMapping("/validate-stock")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<Void> validateCartStock(Authentication authentication) {
        String emailCustomer = authentication.getName();
        Customer customer = customerService.findByEmail(emailCustomer);

        cartService.validateCartStock(customer.getCustomerId());

        return ApiResponse.<Void>builder()
                .message("Tất cả sản phẩm trong giỏ hàng đều đủ số lượng tồn kho")
                .build();
    }




}
