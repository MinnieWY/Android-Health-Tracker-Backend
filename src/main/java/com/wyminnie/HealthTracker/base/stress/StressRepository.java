package com.wyminnie.healthtracker.base.stress;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StressRepository extends JpaRepository<Stress, Long> {
    StressDTO findByUserIdAndDate(Long userId, String date);
}
