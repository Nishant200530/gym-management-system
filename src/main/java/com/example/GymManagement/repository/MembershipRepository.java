package com.example.GymManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.GymManagement.model.Membership;

public interface MembershipRepository extends JpaRepository<Membership, Integer> {
}
