package com.Corporate.Event_Sync.repository;

import com.Corporate.Event_Sync.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Integer> {
}
