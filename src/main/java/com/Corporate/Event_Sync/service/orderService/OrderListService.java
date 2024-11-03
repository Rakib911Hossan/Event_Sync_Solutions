package com.Corporate.Event_Sync.service.orderService;

import com.Corporate.Event_Sync.entity.Order;
import com.Corporate.Event_Sync.exceptions.NotFoundException;
import com.Corporate.Event_Sync.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class OrderListService {

    private final OrderRepository orderRepository;

    // Get all orders
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByStatus(String status) {
        List<Order> orders = orderRepository.findOrdersByStatus(status);

        if (orders.isEmpty()) {
            throw new NotFoundException("No orders found with status: " + status);
        }

        return orders;
    }
}
