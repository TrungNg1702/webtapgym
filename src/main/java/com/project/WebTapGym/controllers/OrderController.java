package com.project.WebTapGym.controllers;

import com.project.WebTapGym.dtos.OrderDTO;
import com.project.WebTapGym.models.Order;
import com.project.WebTapGym.responses.OrderListResponse;
import com.project.WebTapGym.responses.OrderResponse;
import com.project.WebTapGym.services.IOrderService;
import com.project.WebTapGym.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
public class OrderController {

    private final IOrderService orderService;

    @PostMapping("")
    public ResponseEntity<?> createOrder(
            @RequestBody OrderDTO orderDTO,
            BindingResult result){
        try{
            if (result.hasErrors()) {
                List<String> errorMessages =  result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }

            Order orderResponse = orderService.createOrder(orderDTO);
            return ResponseEntity.ok(orderResponse);
         } catch (Exception e){
             return ResponseEntity.badRequest().body(e.getMessage());
         }

    }

    @GetMapping("")
    public ResponseEntity<?> getAllOrders(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ){
        PageRequest pageRequest = PageRequest.of(page, limit,
                Sort.by("id").descending());

        Page<OrderResponse> orders = orderService.getAllOrders(pageRequest);

        int totalPages = orders.getTotalPages();
        List<OrderResponse> orderResponses = orders.getContent();



        return ResponseEntity.ok(OrderListResponse.builder()
                        .orders(orderResponses)
                        .totalPages(totalPages)
                .build());
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getOrderById(@Valid @PathVariable("id") Long orderId){
        try{
            Order existing = orderService.getOrder(orderId);
            return ResponseEntity.ok(existing);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/user/{user_id}")
    public ResponseEntity<?> getOrdersByUserId(
            @Valid @PathVariable("user_id") Long userId)
    {
        try
        {
            List<Order> orders = orderService.findByUserId(userId);
            return ResponseEntity.ok(orders);
        } catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Lay doanh thu
    @GetMapping("/revenue/monthly")
    public ResponseEntity<Map<String, Double>> getMothlyRevenue(
            @RequestParam(name = "year", required = false) Integer year
    ){
        Map<String, Double> revenue;

        if (year != null){
            revenue = orderService.getMonthlyRevenueByYear(year);
        } else {
            revenue = orderService.getMonthlyRevenue();
        }
        return ResponseEntity.ok(revenue);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(
            @Valid @PathVariable Long id,
            @Valid @RequestBody OrderDTO orderDTO )
    {
        try
        {
            Order existingOrder = orderService.updateOrder(id, orderDTO);
            return ResponseEntity.ok(existingOrder);
        } catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam("status") String newStatus) {
        try {
            Order updatedOrder = orderService.updateOrderStatus(id, newStatus);
            return ResponseEntity.ok(updatedOrder);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@Valid @PathVariable Long id){
        // xoa mem
        orderService.deleteOrder(id);
        return ResponseEntity.ok("Xoa don hang thanh cong");
    }
}
