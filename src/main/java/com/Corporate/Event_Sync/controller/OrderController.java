package com.Corporate.Event_Sync.controller;

import com.Corporate.Event_Sync.dto.OrderDTO;
import com.Corporate.Event_Sync.entity.Order;
import com.Corporate.Event_Sync.service.orderService.OrderListService;
import com.Corporate.Event_Sync.service.orderService.OrderService;
import com.Corporate.Event_Sync.utils.Status;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@AllArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private OrderService orderService;
    private OrderListService orderListService;
    // Create a new order
    @PostMapping("/createOrders")
    public ResponseEntity<Order> createOrder(@RequestBody OrderDTO orderDTO) {
        Order order = orderService.createOrder(Math.toIntExact(orderDTO.getUserId()), Math.toIntExact(orderDTO.getMenuItemId()),orderDTO.getStatus());
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<Order> updateOrderById(
            @PathVariable Long orderId,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Status status) {

        Order updatedOrder = orderService.updateOrderById(orderId, userId, status);
        return ResponseEntity.ok(updatedOrder);
    }

    // Get all orders for a specific user
//    @GetMapping("/user/{userId}")
//    public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable Integer userId) {
//        List<Order> orders = orderService.getOrdersByUserId(userId);
//        return ResponseEntity.ok(orders);
//    }

    // Get all orders
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderListService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    // Get a single order by ID
    @GetMapping("/{orderId}")
    public Map<String, Object> getOrderDetails(@PathVariable Long orderId) {
        return orderService.getOrderDetailsById(orderId);
    }

    // Delete an order by ID
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrderById(@PathVariable Long orderId) {
        orderService.deleteOrderById(orderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/name/{userName}")
    public List<Order> getOrdersByUserName(@PathVariable String userName) {
        return orderService.getOrdersByUserName(userName);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable Status status) {
        List<Order> orders = orderListService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }

}
