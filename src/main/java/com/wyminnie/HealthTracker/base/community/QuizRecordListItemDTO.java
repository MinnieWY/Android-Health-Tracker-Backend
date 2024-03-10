package com.wyminnie.healthtracker.base.community;

import java.util.Date;

import lombok.Data;

@Data
public class QuizRecordListItemDTO {
    private long id;
    private long userId;
    private long quizId;
    private boolean isCorrect;
    private Date dateAnswered;

    public static QuizRecordListItemDTO from(QuizRecord quizRecord) {
        QuizRecordListItemDTO dto = new QuizRecordListItemDTO();
        dto.id = quizRecord.getId();
        dto.userId = quizRecord.getUserId();
        dto.quizId = quizRecord.getQuizId();
        dto.isCorrect = quizRecord.isCorrect();
        dto.dateAnswered = quizRecord.getDateAnswered();

        return dto;
    }
}
