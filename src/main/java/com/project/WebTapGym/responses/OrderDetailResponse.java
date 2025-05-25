package com.project.WebTapGym.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.WebTapGym.models.OrderDetail;
import lombok.*;

@AllArgsConstructor
@Builder
@Getter
@Setter
@NoArgsConstructor
public class OrderDetailResponse {
    private Long id;

    @JsonProperty("order_id")
    private Long orderId;

    @JsonProperty("product_id")
    private Long productId;

    private Float price;

    @JsonProperty("number_of_products")
    private int numberOfProducts;

    @JsonProperty("total_money")
    private Float totalMoney;

    public static OrderDetailResponse fromOrderDetail(OrderDetail orderDetail) {
        if (orderDetail == null) {
            return null; // Trả về null nếu orderDetail đầu vào là null
        }

        return OrderDetailResponse.builder()
                .id(orderDetail.getId())
                // Kiểm tra null cho orderDetail.getOrder()
                .orderId(orderDetail.getOrder() != null ? orderDetail.getOrder().getId() : null)
                // Kiểm tra null cho orderDetail.getProduct()
                .productId(orderDetail.getProduct() != null ? orderDetail.getProduct().getId() : null)
                .price(orderDetail.getPrice())
                .numberOfProducts(orderDetail.getNumberOfProducts())
                .totalMoney(orderDetail.getTotalMoney())
                .build();
    }
}