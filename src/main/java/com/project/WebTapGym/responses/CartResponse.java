package com.project.WebTapGym.responses;

import com.project.WebTapGym.models.Cart;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse extends BaseResponse {
    private Long cartId;
    private Long userId;
    private List<CartItemResponse> items;

    public static CartResponse fromCart(Cart cart) {
        CartResponse response = new CartResponse();
        response.setCartId(cart.getId());
        response.setUserId(cart.getUser().getId());
        response.setItems(cart.getCartItems().stream()
                .map(CartItemResponse::fromCartItem)
                .collect(Collectors.toList()));
        return response;
    }
}
