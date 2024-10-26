package com.Corporate.Event_Sync.service.orderService;

import com.Corporate.Event_Sync.entity.MenuItem;
import com.Corporate.Event_Sync.entity.Order;
import com.Corporate.Event_Sync.entity.User;
import com.Corporate.Event_Sync.exceptions.ConflictException;
import com.Corporate.Event_Sync.exceptions.IllegalStateException;
import com.Corporate.Event_Sync.exceptions.NotFoundException;
import com.Corporate.Event_Sync.repository.MenuItemRepository;
import com.Corporate.Event_Sync.repository.OrderRepository;
import com.Corporate.Event_Sync.repository.UserRepository;
import com.Corporate.Event_Sync.utils.Status;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@AllArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final MenuItemRepository menuItemRepository;


    public Order createOrder(Integer userId, Integer menuItemId, Status status) {
        // Fetch the user from the repository
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Check if the user is active
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new IllegalStateException("Order cannot be created. User is inactive.");
        }
        // Fetch the menu item from the repository
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new NotFoundException("Menu item not found"));

        // Create a new order
        Order order = new Order();
        order.setUser(user);  // Set the user
        order.setMenuItem(menuItem); // Set the menu item
        order.setOrderDate(LocalDateTime.now()); // Set current date and time
        order.setStatus(status); // Set status from user input

        // Persist the order
        return orderRepository.save(order);
    }

    public Order updateOrderById(Long orderId, Integer userId, Status status) {
        // Find the order by ID or throw an exception if not found
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order with ID " + orderId + " not found"));
        // If a userId is provided, update the user for the order
        if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));

            // Check if the user is active; if not, throw an exception
            if (!Boolean.TRUE.equals(user.getIsActive())) {
                throw new ConflictException("Cannot assign order to a deactivated user with ID " + userId);
            }
            order.setUser(user);
        }
        // If a status is provided, update the status for the order
        if (status != null) {
            order.setStatus(status);
        }
        // Save and return the updated order
        return orderRepository.save(order);
    }


    //    // Get all orders for a specific user
//    public List<Order> getOrdersByUserId(Integer userId) {
//        return orderRepository.findByUserId(userId);
//    }
    public Map<String, Object> getOrderDetailsById(Long id) {
        return orderRepository.findOrderDetailsByOrderId(id);
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
