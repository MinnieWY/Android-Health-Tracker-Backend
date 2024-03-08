package com.wyminnie.healthtracker.base.community;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wyminnie.healthtracker.base.user.User;
import com.wyminnie.healthtracker.base.user.UserListItemDTO;

@Service
public class CommunityServiceImpl implements CommunityService {
    @Autowired
    FriendRepository friendRepository;
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    QuizRecordRepository quizRecordRepository;

    @Override
    public boolean addFriendRequest(Optional<User> currentUser, Optional<User> targetUser) throws Exception {
        if (!isFriend(currentUser, targetUser)) {
            throw new Exception();
        }
        if (currentUser.isPresent() && targetUser.isPresent()) {
            Friend entity = new Friend();
            entity.setUser1(currentUser.get().getId());
            entity.setUser2(targetUser.get().getId());
            entity.setStatus("PENDING");
            friendRepository.save(entity);
            return true;
        }
        return false;
    }

    @Override
    public boolean isFriend(Optional<User> currentUser, Optional<User> targetUser) {
        return friendRepository.existsByUsers(currentUser, targetUser);
    }

    @Override
    public QuestionDTO getTodayQuestion() {

        Date currentDate = Date.valueOf(LocalDate.now());
        Question questions = questionRepository.findOneByDate(currentDate);
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setQuestion(questions.getQuestionText());
        questionDTO.setId(questions.getId());
        questionDTO
                .setAnswerOptions(Arrays.asList(questions.getOption1(), questions.getOption2(), questions.getOption3(),
                        questions.getOption4()).stream().filter(option -> option != null).collect(Collectors.toList()));

        return questionDTO;

    }

    @Override
    public QuizRecord submitQuizAnswer(QuizAnswerDTO quizAnswerDTO, User user) {
        Question answeredQuestion = questionRepository.findById(Long.valueOf(quizAnswerDTO.getQuizId())).orElse(null);

        if (answeredQuestion == null) {
            throw new RuntimeException("Question not found");
        }

        boolean isCorrect = checkAnswer(answeredQuestion, quizAnswerDTO.getAnswer());
        QuizRecord quizRecord = new QuizRecord();
        quizRecord.setCorrect(isCorrect);
        quizRecord.setUserId(user.getId());
        quizRecord.setQuizId(answeredQuestion.getId());
        quizRecord.setDateAnswered(Date.valueOf(LocalDate.now()));

        return quizRecordRepository.saveAndFlush(quizRecord);
    }

    private boolean checkAnswer(Question answeredQuestion, int selectedOptions) {

        return answeredQuestion.getAnswer() == selectedOptions;
    }

    @Override
    public List<QuizRecordListItemDTO> getQuizRecords(Long userId) {
        return quizRecordRepository.findByUserId(userId).stream().map(QuizRecordListItemDTO::from)
                .collect(Collectors.toList());
    }

    @Override
    public QuizRecordDTO getDetailQuizRecord(Long quizRecordId)
            throws QuizRecordNotFoundException, QuestionNotFoundException {
        QuizRecord quizRecord = quizRecordRepository.findById(quizRecordId).orElse(null);
        if (quizRecord == null) {
            throw new QuizRecordNotFoundException();
        }
        Question question = questionRepository.findById(quizRecord.getQuizId()).orElse(null);
        if (question == null) {
            throw new QuestionNotFoundException();
        }

        QuizRecordDTO quizRecordDTO = new QuizRecordDTO();
        quizRecordDTO.setId(quizRecord.getId());
        quizRecordDTO.setQuizId(quizRecord.getQuizId());
        quizRecordDTO.setQuestion(question.getQuestionText());
        quizRecordDTO.setCorrect(quizRecord.isCorrect());

        Integer correctOptionNo = question.getAnswer();
        switch (correctOptionNo) {
            case 1:
                quizRecordDTO.setAnswer(question.getOption1());
                break;
            case 2:
                quizRecordDTO.setAnswer(question.getOption2());
                break;
            case 3:
                quizRecordDTO.setAnswer(question.getOption3());
                break;
            case 4:
                quizRecordDTO.setAnswer(question.getOption4());
                break;
            default:
                quizRecordDTO.setAnswer("no option");
                break;
        }
        return quizRecordDTO;
    }
}
