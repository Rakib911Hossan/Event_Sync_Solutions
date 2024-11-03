package com.Corporate.Event_Sync.dto.mapper;

import com.Corporate.Event_Sync.dto.OrderDTO;
import com.Corporate.Event_Sync.entity.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    // Convert Order entity to OrderDTO
    public OrderDTO toDTO(Order order) {
        return new OrderDTO(
                order.getId(),              // orderId
                order.getUser().getId(),     // userId
                order.getMenuItem().getId(),       // menuItemId
                order.getOrderDate(),        // orderDate
                order.getStatus()            // status
        );
    }
    private List<OrderDTO> convertOrdersToDTO(List<Order> orders) {
        return orders.stream()
                .map(order -> new OrderDTO(
                        order.getId(),
                        order.getUser().getId(),
                        order.getMenuItem().getId(), // Ensure this method is available in MenuItem
                        order.getOrderDate(),
                        order.getStatus()))
                .toList();
    }
    // Convert a list of Order entities to a list of OrderDTOs
    public List<OrderDTO> toDTOList(List<Order> orders) {
        return orders.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
