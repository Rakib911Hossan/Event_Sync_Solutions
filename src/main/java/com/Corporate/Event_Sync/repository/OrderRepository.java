package com.Corporate.Event_Sync.repository;

import com.Corporate.Event_Sync.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

//    // Fetch all orders for a specific user
//    List<Order> findByUserId(Integer userId);

    @Query("SELECT new map(o.id as Id,u.name as name, u.department as department, u.isActive as isActive, u.officeId as officeId, o.orderDate as orderDate, o.status as status) " +
            "FROM Order o JOIN o.user u WHERE o.id = :id")
    Map<String, Object> findOrderDetailsByOrderId(@Param("id") Long id);


    @Query("SELECT o FROM Order o JOIN o.user u WHERE u.name = :userName")
    List<Order> findOrdersByUserName(@Param("userName") String userName);

}
