package com.project.WebTapGym.services;

import com.project.WebTapGym.dtos.CartItemDTO;
import com.project.WebTapGym.dtos.OrderDTO;
import com.project.WebTapGym.models.*;
import com.project.WebTapGym.repositories.OrderDetailRepository;
import com.project.WebTapGym.repositories.OrderRepository;
import com.project.WebTapGym.repositories.ProductRepository;
import com.project.WebTapGym.repositories.UserRepository;
import com.project.WebTapGym.responses.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;

    @Override
    @Transactional
    public Order createOrder(OrderDTO orderDTO) {
        // check userid co ton tai hay khong
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        Order order = new Order();
        modelMapper.map(orderDTO, order);
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        // kiem tra shipping date >= ngay hom nay
        LocalDate shippingDate = orderDTO.getShippingDate() == null ? LocalDate.now(): orderDTO.getShippingDate();
        if (shippingDate.isBefore(LocalDate.now())) {
            throw new RuntimeException("Date must be after now");
        }
        order.setShippingDate(shippingDate);
        order.setActive(true);
        order.setTotalMoney(orderDTO.getTotalMoney());
        order.setActive(true);
        orderRepository.save(order);
        // Tạo danh sách các đối tượng OrderDetail từ CartItem
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CartItemDTO cartItemDTO : orderDTO.getCartItems()) {
            // Tạo một đối tượng OrderDetail từ CartItemDTO
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);

            // Lấy thông tin sản phẩm từ cartItemDTO
            Long productId = cartItemDTO.getProductId();
            int quantity = cartItemDTO.getQuantity();

            // Tìm thông tin sản phẩm từ csdl
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            // Đặt thông tin cho OrderDetail
            orderDetail.setProduct(product);
            orderDetail.setNumberOfProducts(quantity);

            // Các trường khác nếu cần
            orderDetail.setPrice(product.getPrice());

            // Thêm orderDetail vào danh sách
            orderDetails.add(orderDetail);
        }



        orderDetailRepository.saveAll(orderDetails);
        return order; // ánh xạ
    }

    @Override
    @Transactional
    public Order getOrder(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        order.getOrderDetails().size();
        return order;
    }

    @Override
    @Transactional
    public Order updateOrder(Long id, OrderDTO orderDTO) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        User existingUser = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (orderDTO.getShippingDate() != null) {
            LocalDate shippingDate = orderDTO.getShippingDate();
            if (shippingDate.isBefore(LocalDate.now())) {
                throw new RuntimeException("Shipping date must be after or equal to today.");
            }
            order.setShippingDate(shippingDate);
        }

        // tao bang anh xa
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(
                        mapper -> {
                            mapper.skip(Order::setId);
                            mapper.skip(Order::setShippingDate);
                });
        modelMapper.map(orderDTO, order);
        order.setUser(existingUser);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(null);
        if(order != null) {
            order.setActive(false);
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
        }
    }


    @Override
    @Transactional
    public List<Order> findByUserId(Long userId) {
        // Kiểm tra user có tồn tại không trước khi tìm order (tùy chọn, nhưng tốt)
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Sử dụng phương thức mới để tải đầy đủ các mối quan hệ
        return orderRepository.findByUserId(userId);
    }

    @Override
    public Order updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        if (order.getStatus().equals(OrderStatus.CANCELLED)) {
            throw new RuntimeException("Cannot change status for an order that is already CANCELLED.");
        }

        if (!isValidOrderStatus(newStatus)) {
            throw new RuntimeException("Invalid order status: " + newStatus);
        }

        order.setStatus(newStatus);

        return orderRepository.save(order);
    }

    @Override
    public Page<OrderResponse> getAllOrders(PageRequest pageRequest) {
        return orderRepository
                .findAll(pageRequest)
                .map(OrderResponse::fromOrder);
    }


    private boolean isValidOrderStatus(String status) {
        return status.equals(OrderStatus.PENDING) ||
                status.equals(OrderStatus.PROCESSING) ||
                status.equals(OrderStatus.SHIPPED) ||
                status.equals(OrderStatus.DELIVERED) ||
                status.equals(OrderStatus.CANCELLED);
    }

    public Map<String, Double> getMonthlyRevenue() {
        List<Object[]> results = orderRepository.findMonthlyRevenue();
        Map<String, Double> monthlyRevenue = new HashMap<>();

        for (Object[] row : results) {
            Integer year = (Integer) row[0];
            Integer month = (Integer) row[1];
            Double totalRevenue = (Double) row[2];
            String monthYear = String.format("%02d/%d", month, year); // Định dạng MM/YYYY
            monthlyRevenue.put(monthYear, totalRevenue);
        }
        return monthlyRevenue;
    }

    public Map<String, Double> getMonthlyRevenueByYear(int year) {
        List<Object[]> results = orderRepository.findMonthlyRevenueByYear(year);
        Map<String, Double> mothlyRevenue = new HashMap<>();

        for (Object[] row : results) {
            Integer month = (Integer) row[0];
            Double totalRevenue = (Double) row[1];
            String monthString = String.format("%02d", month);
            mothlyRevenue.put(monthString, totalRevenue);
        }
        return mothlyRevenue;
    }

    @Override
    public void deleteOrderByAdmin(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (order != null){
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
        }
    }

}
