package com.example.EVCharge.repository;

import com.example.EVCharge.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.EVCharge.models.Role;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    List<User> findAllByRole(Role role);
}

