package com.example.GymManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.GymManagement.model.ContactMessage;

public interface ContactRepository extends JpaRepository<ContactMessage, Integer> {
}
