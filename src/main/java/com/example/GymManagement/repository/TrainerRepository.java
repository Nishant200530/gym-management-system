package com.example.GymManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.GymManagement.model.Trainer;

public interface TrainerRepository extends JpaRepository<Trainer, Integer> {
}
