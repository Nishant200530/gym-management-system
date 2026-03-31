package com.example.GymManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.GymManagement.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    // 🔥 ADD THIS
    void deleteByUserId(int userId);
}
