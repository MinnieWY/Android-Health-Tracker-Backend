package com.wyminnie.healthtracker.base.community;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizRecordRepository extends JpaRepository<QuizRecord, Long> {
    public List<QuizRecord> findByUserId(Long userId);
}
