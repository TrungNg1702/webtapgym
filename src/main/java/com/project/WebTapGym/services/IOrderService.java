package com.project.WebTapGym.services;

import com.project.WebTapGym.dtos.OrderDTO;
import com.project.WebTapGym.models.Order;
import com.project.WebTapGym.responses.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IOrderService {
    Order createOrder(OrderDTO orderDTO);

    Order getOrder(Long id);

    Order updateOrder(Long id,OrderDTO orderDTO);

    void deleteOrder(Long id);

    List<Order> findByUserId(Long userId);

    Order updateOrderStatus(Long orderId, String newStatus);

    Page<OrderResponse> getAllOrders(PageRequest pageRequest);

    Map<String, Double> getMonthlyRevenue();

    Map<String, Double> getMonthlyRevenueByYear(int year);

    void deleteOrderByAdmin(Long id);
}
