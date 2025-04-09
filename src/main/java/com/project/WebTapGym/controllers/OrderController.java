package com.project.WebTapGym.controllers;

import com.project.WebTapGym.dtos.OrderDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/orders")
public class OrderController {

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

            return ResponseEntity.ok("Them moi order thanh cong");
         } catch (Exception e){
             return ResponseEntity.badRequest().body(e.getMessage());
         }

    }

    @GetMapping("")
    public ResponseEntity<?> getAllOrders(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ){
        return ResponseEntity.ok("asdasd");
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<?> getOrdersByUserId(
            @Valid @PathVariable("user_id") Long userId)
    {
        try
        {

            return ResponseEntity.ok("Orders of user with id: " + userId + " found");
        } catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(
            @Valid @PathVariable Long id,
            @Valid @RequestBody OrderDTO orderDTO )
    {
        return ResponseEntity.ok("Cap nhat thong tin");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@Valid @PathVariable Long id){
        // xoa mem

        return ResponseEntity.ok("Xoa don hang thanh cong");
    }
}
