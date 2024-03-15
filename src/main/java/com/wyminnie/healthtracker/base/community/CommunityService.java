package com.wyminnie.healthtracker.base.community;

import java.util.List;
import java.util.Optional;

import com.wyminnie.healthtracker.base.user.User;

public interface CommunityService {

    public QuestionDTO getTodayQuestion();

    public QuizRecordDTO submitQuizAnswer(QuizAnswerDTO quizAnswerDTO, User user) throws QuestionNotFoundException;

    public List<QuizRecordListItemDTO> getQuizRecords(Long userId);

    public QuizRecordDTO getDetailQuizRecord(Long quizRecordId)
            throws QuizRecordNotFoundException, QuestionNotFoundException;
}
