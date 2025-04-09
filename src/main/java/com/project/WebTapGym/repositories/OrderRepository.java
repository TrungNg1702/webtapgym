package com.project.WebTapGym.repositories;

import com.project.WebTapGym.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // tim don hang cua 1 user
    List<Order> findByUserId(Long userId);
}
