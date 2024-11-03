package com.Corporate.Event_Sync.repository;

import com.Corporate.Event_Sync.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Integer> {
    @Query(value = "SELECT * FROM menu_items WHERE category = category", nativeQuery = true)
    List<MenuItem> findByCategory(@Param("category") String category);

    @Query(value = "SELECT m.id, m.item_name, m.description, m.category, m.available_time FROM menu_items m", nativeQuery = true)
    List<Object[]> findAllMenuItems();

    @Query(value = "SELECT m.item_name FROM menu_items m", nativeQuery = true)
    List<String> findAllMenuItemNames();
}
