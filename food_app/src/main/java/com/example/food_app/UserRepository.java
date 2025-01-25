package com.example.food_app;



import com.example.food_app.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhoneNumberAndPassword(String phoneNumber, String password);

    Optional<User> findByPhoneNumber(String phoneNumber);
}
