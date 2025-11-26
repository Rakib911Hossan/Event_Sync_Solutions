package com.Corporate.Event_Sync.service.orderService;

import com.Corporate.Event_Sync.dto.OrderDTO;
import com.Corporate.Event_Sync.dto.mapper.OrderMapper;
import com.Corporate.Event_Sync.entity.MenuItem;
import com.Corporate.Event_Sync.entity.Order;
import com.Corporate.Event_Sync.entity.User;
import com.Corporate.Event_Sync.repository.MenuItemRepository;
import com.Corporate.Event_Sync.repository.OrderRepository;
import com.Corporate.Event_Sync.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
@AllArgsConstructor
@Service
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final MenuItemRepository menuItemRepository;

    private final OrderMapper orderMapper;

    // Get all orders for a specific user
    public List<OrderDTO> getOrdersByUserId(Integer userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orderMapper.toDTOList(orders);
    }
    public void createOrder(Integer userId, Integer menuItemId, String status,
                            LocalDateTime orderDate, double latitude, double longitude, Integer price) {

        // Step 1: Create the order
        orderRepository.createOrder(userId, menuItemId, status, orderDate, latitude, longitude, price);

        // Step 2: Get total number of orders for the user
        long orderCount = orderRepository.countByUserId(userId);

        // Step 3: Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Step 4: Update user category
        if (orderCount >= 10) {
            user.setUserCategory("PREMIUM");
        } else if (orderCount >= 5) {
            user.setUserCategory("REGULAR");
        } else {
            user.setUserCategory("NORMAL");
        }

        // Step 5: Save updated user
        userRepository.save(user);
    }
    // In your repository - just use the inherited save method
// No need for custom query

    // In your service:
    public Order saveOrder(Integer userId, Integer menuItemId, String status,
                           LocalDateTime orderDate, double latitude,
                           double longitude, Integer price) {
        Order order = new Order();

        // Set user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        order.setUser(user);

        // Set menu item
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("MenuItem not found"));
        order.setMenuItem(menuItem);

        order.setStatus(status);
        order.setOrderDate(orderDate);
        order.setLatitude(latitude);
        order.setLongitude(longitude);
        order.setPrice(price);

        Order savedOrder = orderRepository.save(order);

        long orderCount = orderRepository.countByUserId(userId);

        // Step 4: Update user category
        if (orderCount >= 10) {
            user.setUserCategory("PREMIUM");
        } else if (orderCount >= 5) {
            user.setUserCategory("REGULAR");
        } else {
            user.setUserCategory("NORMAL");
        }

        // Step 5: Save updated user
        userRepository.save(user);

        return savedOrder;
    }
    @Transactional
    public void updateOrder(Order order) {
        orderRepository.save(order);
    }

    @Transactional
    public OrderDTO updateOrderById(Integer orderId, Integer menuItemId, String status, LocalDateTime orderDate) {
        Order order = orderRepository.findById(Long.valueOf(orderId)).orElse(null);

        // Update menuItem
        MenuItem menuItem = menuItemRepository.findById(Math.toIntExact(Long.valueOf(menuItemId))).orElse(null);
        if (order != null && menuItem != null) {
            order.setMenuItem(menuItem);
            order.setStatus(status);
            order.setOrderDate(orderDate);

            Order updatedOrder = orderRepository.save(order);
            return orderMapper.toDTO(updatedOrder);
        }
        return null; // Return null if order or menuItem is not found
    }

    @Transactional
    public OrderDTO updateOrderByLongLat(Integer orderId, double latitude, double longitude) {
        Order order = orderRepository.findById(Long.valueOf(orderId)).orElse(null);

        if (order != null) {
            order.setLatitude(latitude);
            order.setLongitude(longitude);

            Order updatedOrder = orderRepository.save(order);
            return orderMapper.toDTO(updatedOrder);
        }
        return null; // Return null if order or menuItem is not found
    }

    public Map<String, Object> getOrderDetailsById(Long id) {
        return orderRepository.findOrderDetailsByOrderId(id);
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow();
    }

    public void deleteOrderById(Long orderId) {
        orderRepository.deleteById(orderId);
    }

    public List<Order> getOrdersByUserName(String userName) {
        return orderRepository.findOrdersByUserName(userName);
    }

    @Transactional
    public void deleteOrdersByUserId(Integer userId) {
        orderRepository.deleteByUserId(userId);
    }
}
