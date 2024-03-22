package com.wyminnie.healthtracker.base.stress;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StressRepository extends JpaRepository<Stress, Long> {
    Stress findByUserIdAndDate(Long userId, String date);
}
