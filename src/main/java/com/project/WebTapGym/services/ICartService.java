package com.project.WebTapGym.services;

import com.project.WebTapGym.models.Cart;
import com.project.WebTapGym.responses.CartResponse;

public interface ICartService {
    Cart addProductToCart (Long userId, Long productId, Integer quantity);

    CartResponse getCartByUserId(Long userId);

    Cart updateCartItemQuantity(Long userId, Long productId, Integer quantity);

    void removeProductFromCart(Long userId, Long productId);

    void clearCart(Long userId);
}
