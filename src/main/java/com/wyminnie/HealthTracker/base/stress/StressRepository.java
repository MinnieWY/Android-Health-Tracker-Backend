package com.wyminnie.healthtracker.base.stress;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StressRepository extends JpaRepository<Stress, Long> {
    Stress findByUserIdAndDate(Long userId, String date);

    List<Stress> findByUserIdAndMonthAndYearOrderByDate(Long userId, int month, int year);
}
