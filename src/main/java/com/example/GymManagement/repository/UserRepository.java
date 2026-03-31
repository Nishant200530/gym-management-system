package com.example.GymManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.GymManagement.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    // used for login & forgot password
    User findByEmail(String email);
}
