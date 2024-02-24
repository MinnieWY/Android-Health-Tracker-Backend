package com.wyminnie.healthtracker.base.community;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizRecordRepository extends JpaRepository<QuizRecord, Long> {

}
