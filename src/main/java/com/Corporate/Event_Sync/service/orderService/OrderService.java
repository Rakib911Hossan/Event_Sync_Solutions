package com.Corporate.Event_Sync.service.orderService;

import com.Corporate.Event_Sync.entity.Order;
import com.Corporate.Event_Sync.entity.User;
import com.Corporate.Event_Sync.exceptions.NotFoundException;
import com.Corporate.Event_Sync.repository.OrderRepository;
import com.Corporate.Event_Sync.repository.UserRepository;
import com.Corporate.Event_Sync.utils.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    public Order createOrder(Integer userId, Status status) {
        // Fetch the user from the repository
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Create a new order
        Order order = new Order();
        order.setUser(user); // Set the user
        order.setOrderDate(LocalDateTime.now()); // Set current date and time
        order.setStatus(status); // Set status from user input

        return orderRepository.save(order); // Persist the order
    }
//    // Get all orders for a specific user
//    public List<Order> getOrdersByUserId(Integer userId) {
//        return orderRepository.findByUserId(userId);
//    }
    public Map<String, Object> getOrderDetailsById(Long id) {
        return orderRepository.findOrderDetailsByOrderId(id);
    }
    // Get all orders
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Get order by ID
    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    // Delete order by ID
    public void deleteOrderById(Long orderId) {
        orderRepository.deleteById(orderId);
    }

    public List<Order> getOrdersByUserName(String userName) {
        return orderRepository.findOrdersByUserName(userName);
    }

}
