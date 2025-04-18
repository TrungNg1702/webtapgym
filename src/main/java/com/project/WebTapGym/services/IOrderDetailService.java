package com.project.WebTapGym.services;

import com.project.WebTapGym.dtos.OrderDetailDTO;
import com.project.WebTapGym.exceptions.DataNotFoundException;
import com.project.WebTapGym.models.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IOrderDetailService {
    OrderDetail createOrderDetail(OrderDetailDTO newOrderDetail) throws DataNotFoundException;

    OrderDetail getOrderDetail(Long id) throws DataNotFoundException;

    OrderDetail updateOrderDetail(Long id,OrderDetailDTO newOrderDetail) throws DataNotFoundException;

    void deleteOrderDetail(Long id) throws DataNotFoundException;

    List<OrderDetail> findByOrderId(Long orderId);
}
