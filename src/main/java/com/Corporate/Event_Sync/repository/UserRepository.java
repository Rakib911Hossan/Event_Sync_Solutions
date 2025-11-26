package com.Corporate.Event_Sync.repository;

import com.Corporate.Event_Sync.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

        User findByEmail(String email);

        List<User> findByIsActive(boolean isActive);

        @Modifying
        @Transactional
        @Query(value = "UPDATE \"user\" SET name = :name, phone = :phone, email = :email, address = :address, " +
                "department = :department, role = :role, is_active = :isActive, office_id = :officeId, user_pic = :userPic, " +
                "pass_token = :passToken, discount_token = :discountToken, discount_amount = :discountAmount WHERE id = :id", nativeQuery = true)
        void updateUserById(Integer id, String name, String phone, String email, String address, String department, String role,
                            Boolean isActive, Integer officeId, String userPic, String passToken, String discountToken, Integer discountAmount);
}



