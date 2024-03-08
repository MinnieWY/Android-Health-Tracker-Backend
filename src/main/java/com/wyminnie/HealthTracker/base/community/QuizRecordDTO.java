package com.wyminnie.healthtracker.base.community;

import lombok.Setter;

@Setter
public class QuizRecordDTO {
    private long id;
    private long quizId;
    private String question;
    private boolean isCorrect;
    private String answer;
}
