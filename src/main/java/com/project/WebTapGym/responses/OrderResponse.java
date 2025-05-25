package com.project.WebTapGym.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.WebTapGym.models.Order;
import com.project.WebTapGym.models.OrderDetail; // Giữ nguyên import này
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
// import java.util.Date; // Không cần thiết nếu bạn đã dùng LocalDateTime và LocalDate
import java.util.List;
import java.util.stream.Collectors; // Thêm import này

@AllArgsConstructor
@Builder
@Getter
@Setter
@NoArgsConstructor
public class OrderResponse extends BaseResponse {
    private Long id;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("full_name")
    private String fullName;

    private String email;

    private String phone;

    private String address;

    private String note;

    @JsonProperty("order_date")
    private LocalDateTime orderDate;

    private String status;

    @JsonProperty("total_money")
    private Float totalMoney;

    @JsonProperty("shipping_method")
    private String shippingMethod;

    @JsonProperty("shipping_address")
    private String shippingAddress;

    @JsonProperty("shipping_date")
    private LocalDate shippingDate;

    @JsonProperty("tracking_number")
    private String trackingNumber;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("active")
    private Boolean active;

    @JsonProperty("order_details")
    private List<OrderDetailResponse> orderDetails;

    public static OrderResponse fromOrder(Order order) {
        if (order == null) {
            return null;
        }

        // Xử lý orderDetails
        List<OrderDetailResponse> orderDetailResponses = null;
        if (order.getOrderDetails() != null) {
            orderDetailResponses = order.getOrderDetails()
                    .stream()
                    .map(OrderDetailResponse::fromOrderDetail)
                    .collect(Collectors.toList());
        }

        OrderResponse orderResponse = OrderResponse
                .builder()
                .id(order.getId())
                // Kiểm tra null cho order.getUser()
                .userId(order.getUser() != null ? order.getUser().getId() : null)
                .fullName(order.getFullName())
                .phone(order.getPhone())
                .email(order.getEmail())
                .address(order.getAddress())
                .note(order.getNote())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .totalMoney(order.getTotalMoney())
                .shippingMethod(order.getShippingMethod())
                .shippingAddress(order.getShippingAddress())
                .shippingDate(order.getShippingDate())
                .paymentMethod(order.getPaymentMethod())
                .orderDetails(orderDetailResponses)
                .active(order.getActive())
                .build();
        return orderResponse;
    }
}