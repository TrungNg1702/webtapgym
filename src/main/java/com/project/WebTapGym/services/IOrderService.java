package com.project.WebTapGym.services;

import com.project.WebTapGym.dtos.OrderDTO;
import com.project.WebTapGym.models.Order;
import com.project.WebTapGym.responses.OrderResponse;

import java.util.List;

public interface IOrderService {
    OrderResponse createOrder(OrderDTO orderDTO);

    Order getOrder(Long id);

    Order updateOrder(Long id,OrderDTO orderDTO);

    void deleteOrder(Long id);

    List<Order> findByUserId(Long userId);
}
