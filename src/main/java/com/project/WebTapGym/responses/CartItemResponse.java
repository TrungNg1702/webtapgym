package com.project.WebTapGym.responses;
import com.project.WebTapGym.models.CartItem;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponse {
    private Long id;
    private ProductResponse product;
    private Integer quantity;

    public static CartItemResponse fromCartItem(CartItem cartItem) {
        CartItemResponse response = new CartItemResponse();
        response.setId(cartItem.getId());
        response.setProduct(ProductResponse.from(cartItem.getProduct()));
        response.setQuantity(cartItem.getQuantity());
        return response;
    }
}
