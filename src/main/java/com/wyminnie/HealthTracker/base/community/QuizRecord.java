package com.wyminnie.healthtracker.base.community;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "quiz_record")
public class QuizRecord {
    @Id
    private long id;
    @Column(name = "user_id")
    private long userId;
    @Column(name = "quiz_id")
    private long quizId;
    @Column(name = "is_correct")
    private boolean isCorrect;
    private Date dateAnswered;
}
