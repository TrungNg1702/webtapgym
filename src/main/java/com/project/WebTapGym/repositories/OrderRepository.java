package com.project.WebTapGym.repositories;

import com.project.WebTapGym.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Objects;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // tim don hang cua 1 user
    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.orderDetails od " +
            "LEFT JOIN FETCH od.product " +
            "LEFT JOIN FETCH o.user " +
            "WHERE o.user.id = :userId")
    List<Order> findByUserId(@Param("userId") Long userId);


    // [năm, tháng, tổng doanh thu]
    @Query("SELECT YEAR(o.orderDate), MONTH(o.orderDate), SUM(o.totalMoney) " +
            "FROM Order o " +
            "GROUP BY YEAR(o.orderDate), MONTH(o.orderDate) " +
            "ORDER BY YEAR(o.orderDate) ASC, MONTH(o.orderDate) ASC")
    List<Object[]> findMonthlyRevenue();

    @Query("SELECT MONTH(o.orderDate), SUM(o.totalMoney) " +
            "FROM Order o " +
            "WHERE YEAR(o.orderDate) = :year " +
            "GROUP BY MONTH(o.orderDate) " +
            "ORDER BY MONTH(o.orderDate) ASC")
    List<Object[]> findMonthlyRevenueByYear(int year);
}
