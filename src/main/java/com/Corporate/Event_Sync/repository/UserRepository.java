package com.Corporate.Event_Sync.repository;

import com.Corporate.Event_Sync.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

}
