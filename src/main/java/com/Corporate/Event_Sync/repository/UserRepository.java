package com.Corporate.Event_Sync.repository;

import com.Corporate.Event_Sync.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

        User findByEmail(String email);
//        Optional<User> findById(Integer id);

        @Query("SELECT u FROM User u LEFT JOIN FETCH u.orders WHERE u.id = :id")
        User findUserWithOrdersById(@Param("id") Integer id);



}

