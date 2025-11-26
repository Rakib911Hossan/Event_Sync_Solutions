package com.Corporate.Event_Sync.repository;

import com.Corporate.Event_Sync.entity.MenuItem;
import com.Corporate.Event_Sync.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query(value = "SELECT * FROM orders WHERE user_id = :userId", nativeQuery = true)
    List<Order> findByUserId(@Param("userId") Integer userId);

    @Query("SELECT new map(o.id as Id,u.name as name, u.department as department, u.isActive as isActive, u.officeId as officeId, o.orderDate as orderDate, o.status as status) " +
            "FROM Order o JOIN o.user u WHERE o.id = :id")
    Map<String, Object> findOrderDetailsByOrderId(@Param("id") Long id);


    @Query("SELECT o FROM Order o JOIN o.user u WHERE u.name = :userName")
    List<Order> findOrdersByUserName(@Param("userName") String userName);

    @Query("SELECT o FROM Order o WHERE o.status = :status")
    List<Order> findOrdersByStatus(@Param("status") String status);

    @Transactional
    @Modifying
    @Query("DELETE FROM Order o WHERE o.user.id IN (SELECT u.id FROM User u WHERE u.isActive = false)")
    void deleteOrdersByInactiveUsers();

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO orders (user_id, menu_item_id, status, order_date, latitude, longitude, price) " +
            "VALUES (:userId, :menuItemId, :status, :orderDate, :latitude, :longitude, :price)", nativeQuery = true)
    int createOrder(
            @Param("userId") Integer userId,
            @Param("menuItemId") Integer menuItemId,
            @Param("status") String status,
            @Param("orderDate") LocalDateTime orderDate,
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("price") Integer price);

    @Modifying
    @Query("UPDATE Order o SET o.orderDate = :orderDate, o.status = :status, o.menuItem = :menuItem WHERE o.id = :orderId")
    void updateOrderDateStatusAndMenuItem(@Param("orderId") Long orderId,
                                          @Param("orderDate") LocalDateTime orderDate,
                                          @Param("status") String status,
                                          @Param("menuItem") MenuItem menuItem);



    @Modifying
    @Query("DELETE FROM Order o WHERE o.user.id = :userId")
    void deleteByUserId(@Param("userId") Integer userId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId")
    long countByUserId(@Param("userId") Integer userId);

}

