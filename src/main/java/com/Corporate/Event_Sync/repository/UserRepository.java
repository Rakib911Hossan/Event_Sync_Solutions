package com.Corporate.Event_Sync.repository;

import com.Corporate.Event_Sync.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

        User findByEmail(String email);
//        Optional<User> findById(Integer id);
        // Add this method to find active users
        List<User> findByIsActive(boolean isActive);



}

