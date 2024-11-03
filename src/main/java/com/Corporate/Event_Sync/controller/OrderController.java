package com.Corporate.Event_Sync.controller;

import com.Corporate.Event_Sync.dto.OrderDTO;
import com.Corporate.Event_Sync.entity.Order;
import com.Corporate.Event_Sync.service.orderService.OrderListService;
import com.Corporate.Event_Sync.service.orderService.OrderService;
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
    public ResponseEntity<Void> createOrder(@RequestBody OrderDTO orderRequest) {
        // Call the service method to create the order
        orderService.createOrder(
                orderRequest.getUserId(),
                orderRequest.getMenuItemId(),
                orderRequest.getStatus(),
                orderRequest.getOrderDate()
        );
        // Return a response indicating success
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @PutMapping("/{orderId}")
    public OrderDTO updateOrder(
            @PathVariable Long orderId,
            @RequestBody OrderDTO orderDTO) {

        return orderService.updateOrderById(
                Math.toIntExact(orderId),  // Convert orderId from Long to Integer
                orderDTO.getMenuItemId(),  // Get menuItemId from OrderDTO
                orderDTO.getStatus(),       // Get status from OrderDTO
                orderDTO.getOrderDate()     // Get orderDate from OrderDTO
        );
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
//    @DeleteMapping("/user/{userId}")
//    public ResponseEntity<Void> deleteOrdersByUserId(@PathVariable Integer userId) {
//        orderService.deleteOrdersByUserId(userId);
//        return ResponseEntity.noContent().build();
//    }

    @GetMapping("/user/name/{userName}")
    public List<Order> getOrdersByUserName(@PathVariable String userName) {
        return orderService.getOrdersByUserName(userName);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable String status) {
        List<Order> orders = orderListService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByUserId(@PathVariable Integer userId) {
        List<OrderDTO> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }
}
