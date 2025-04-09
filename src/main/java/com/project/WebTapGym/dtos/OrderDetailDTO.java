package com.project.WebTapGym.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderDetailDTO {
    @JsonProperty("order_id")
    @Min(value = 1, message = "Order's Id phai lon hon 0")
    private Long orderId;

    @JsonProperty("product_id")
    @Min(value = 1, message = "Product's Id phai lon hon 0")
    private Long productId;

    @Min(value = 0, message = "Gia phai lon hon hoac bang 0")
    private Float price;

    @JsonProperty("number_of_products")
    @Min(value = 1, message = "So luong san pham phai lon hon 0")
    private int numberOfProducts;

    @JsonProperty("total_money")
    @Min(value = 0, message = "Tong tien phai lon hon hoac bang 0")
    private int totalMoney;


}
