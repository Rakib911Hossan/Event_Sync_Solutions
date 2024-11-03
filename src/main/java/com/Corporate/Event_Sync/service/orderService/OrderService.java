package com.Corporate.Event_Sync.service.orderService;

import com.Corporate.Event_Sync.dto.OrderDTO;
import com.Corporate.Event_Sync.dto.mapper.OrderMapper;
import com.Corporate.Event_Sync.entity.MenuItem;
import com.Corporate.Event_Sync.entity.Order;
import com.Corporate.Event_Sync.exceptions.NotFoundException;
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
import java.util.Optional;
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
    public void createOrder(Integer userId, Integer menuItemId, String status, LocalDateTime orderDate) {
        // You can add any necessary checks here (e.g., user existence, menu item existence, user active status)
        // Save the order using the native query
        orderRepository.saveOrder(userId, menuItemId, status, orderDate);
    }
    @Transactional
    public OrderDTO updateOrderById(Integer orderId, Integer menuItemId, String status, LocalDateTime orderDate) {
        Order order = orderRepository.findById(Long.valueOf(orderId))
                .orElseThrow(() -> new NotFoundException("Order with ID " + orderId + " not found"));

            if (menuItemId != null) {
                MenuItem menuItem = menuItemRepository.findById((int) menuItemId.longValue())
                        .orElseThrow(() -> new NotFoundException("Menu item with ID " + menuItemId + " not found"));
                order.setMenuItem(menuItem);
            }
        if (status != null) {
            order.setStatus(status);
        }
        if (orderDate != null) {
            order.setOrderDate(orderDate);
        }
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toDTO(updatedOrder);
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

    @Transactional
    public void deleteOrdersByUserId(Integer userId) {
        orderRepository.deleteByUserId(userId);
    }
}
