package com.wyminnie.healthtracker.base.stress;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StressRepository extends JpaRepository<Stress, Long> {
    Stress findByUserIdAndDate(Long userId, LocalDate date);

    List<Stress> findByUserIdAndMonthAndYearOrderByDate(Long userId, int month, int year);

    List<Stress> findByUserIdAndDateBetween(long userId, LocalDate startDate, LocalDate endDate);
}
