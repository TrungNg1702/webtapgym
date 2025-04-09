package com.project.WebTapGym.controllers;

import com.project.WebTapGym.dtos.OrderDetailDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/order_details")
public class OrderDetailController
{
    // Them moi 1 order detail
    @PostMapping("")
    public ResponseEntity<?> createOrderDetail(
            @Valid @RequestBody OrderDetailDTO orderDetailDTO
    )
    {
        return ResponseEntity.ok("Them moi order detail thanh cong");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetail(
            @Valid @PathVariable Long id
    ){
        return ResponseEntity.ok("Order detail with id: " + id + " found");
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getOrderDetails(
            @Valid @PathVariable("orderId") Long orderId
    ){
//        List<OrderDetail> orderDetails = orderDetailService.getOrderDetails(orderId)
//        return ResponseEntity.ok(orderDetails);
        return  ResponseEntity.ok("get order");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrderDetail(
            @Valid @PathVariable Long id,
            @Valid @RequestBody OrderDetailDTO orderDetailDTO
    )
    {
        return ResponseEntity.ok("Cap nhat thong tin");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderDetail(
            @Valid @PathVariable("id") Long id)
    {
        return ResponseEntity.noContent().build();
    }
}
