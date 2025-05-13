package com.project.WebTapGym.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderDTO {
    @JsonProperty("user_id")
    @Min(value = 1, message = "User id phải lớn hơn 0")
    private  Long userId;

    @JsonProperty("full_name")
    private String fullName;

    private String email;

    @NotNull(message = "Số điện thoại không được để trống")
    private String phone;

    private String address;

    private String note;

    @JsonProperty("total_money")
    @Min(value = 0, message = "Tổng số tiền phải >= 0")
    private Float totalMoney;

    @JsonProperty("shipping_method")
    private String shippingMethod = "";

    @JsonProperty("shipping_address")
    private String shippingAddress = "";

    @JsonProperty("shipping_date")
    private LocalDate shippingDate;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("cart_items")
    private List<CartItemDTO> cartItems;
}
