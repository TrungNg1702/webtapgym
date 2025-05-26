package com.project.WebTapGym.controllers;


import com.project.WebTapGym.models.*;
import com.project.WebTapGym.responses.CartResponse;
import com.project.WebTapGym.services.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/carts")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping("/add/{userId}")
    public ResponseEntity<CartResponse> addProductToCart(
            @PathVariable Long userId,
            @RequestParam Long productId,
            @RequestParam Integer quantity)
    {
        Cart cart = cartService.addProductToCart(userId, productId, quantity);
        return ResponseEntity.ok(CartResponse.fromCart(cart));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<CartResponse> getCartByUserId(@PathVariable Long userId) {
        CartResponse cartResponse = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(cartResponse);
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<CartResponse> updateCartItemQuantity(
            @PathVariable Long userId,
            @RequestParam Long productId,
            @RequestParam Integer quantity
    ){
        Cart cart = cartService.updateCartItemQuantity(userId, productId, quantity);
        return ResponseEntity.ok(CartResponse.fromCart(cart));
    }

    @DeleteMapping("/remove/{userId}/{productId}")
    public ResponseEntity<?> removeProductFromCart(
            @PathVariable Long userId,
            @PathVariable Long productId
    ){
        cartService.removeProductFromCart(userId, productId);
        return ResponseEntity.ok("Xóa sản phẩm thành công");
    }

    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<String> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok("Xóa giỏ hàng thành công");
    }
}
