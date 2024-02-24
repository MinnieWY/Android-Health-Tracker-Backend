package com.wyminnie.healthtracker.base.community;

import java.util.Optional;

import com.wyminnie.healthtracker.base.user.User;

public interface CommunityService {

    public boolean addFriendRequest(Optional<User> currentUser, Optional<User> targetUser) throws Exception;

    public boolean isFriend(Optional<User> currentUser, Optional<User> targetUser);

    public QuestionDTO getTodayQuestion();

    public QuizRecord submitQuizAnswer(QuizAnswerDTO quizAnswerDTO, User user);
}
