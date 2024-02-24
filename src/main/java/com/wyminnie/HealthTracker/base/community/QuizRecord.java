package com.wyminnie.healthtracker.base.community;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "quiz_record")
public class QuizRecord {
    private long id;
    private long userId;
    private long quizId;
    private boolean isCorrect;
    private Date dateAnswered;
}
