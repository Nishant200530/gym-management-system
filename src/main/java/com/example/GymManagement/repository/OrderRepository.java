package com.example.GymManagement.repository;

import com.example.GymManagement.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUsername(String username);
}
