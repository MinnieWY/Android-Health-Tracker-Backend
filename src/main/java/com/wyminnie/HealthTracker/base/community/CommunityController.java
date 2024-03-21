package com.wyminnie.healthtracker.base.community;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wyminnie.healthtracker.base.user.User;
import com.wyminnie.healthtracker.base.user.UserDTO;
import com.wyminnie.healthtracker.base.user.UserService;
import com.wyminnie.healthtracker.common.UserIDDTO;

import static com.wyminnie.healthtracker.common.ControllerUtils.fail;
import static com.wyminnie.healthtracker.common.ControllerUtils.notFound;
import static com.wyminnie.healthtracker.common.ControllerUtils.ok;
import static com.wyminnie.healthtracker.common.ControllerUtils.userNotFound;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class CommunityController {
    @Autowired
    private UserService userService;

    @Autowired
    private CommunityService communityService;

    @GetMapping(value = "/query={query}")
    public Object searchUsers(@PathVariable("query") String query) {
        return ok(userService.searchUsers(query));
    }

    @GetMapping("/search")
    public Object getUserProfile(@RequestParam String targetid) {
        User friend = userService.findUserById(Long.parseLong(targetid)).orElse(null);

        if (friend == null) {
            return userNotFound();
        }

        return ok(userService.getUserPublicProfile(friend.getId()));
    }

    @GetMapping("/quiz/question")
    public Object getTodayQuestion() {
        QuestionDTO question = communityService.getTodayQuestion();

        if (question != null) {
            return ok(question);
        } else {
            return notFound();
        }
    }

    @PostMapping("/quiz/answer")
    public Object submitQuizAnswer(@RequestBody QuizAnswerDTO answer) {
        User user = userService.findUserById(Long.parseLong(answer.getUserId())).orElse(null);
        if (user == null) {
            return notFound();
        }
        try {
            return ok(communityService.submitQuizAnswer(answer, user));
        } catch (QuestionNotFoundException e) {
            return notFound();
        }
    }

    @GetMapping("/quiz/record/user/{userId}")
    public Object getQuizRecordList(@PathVariable("userId") String userId) {
        User user = userService.findUserById(Long.parseLong(userId)).orElse(null);
        if (user == null) {
            return notFound();
        }
        return ok(communityService.getQuizRecords(user.getId()));
    }

    @GetMapping("/quiz/record/{quizRecordId}")
    public Object getQuizRecord(@PathVariable("quizRecordId") String quizRecordId) {

        try {
            return ok(communityService.getDetailQuizRecord(Long.parseLong(quizRecordId)));
        } catch (QuizRecordNotFoundException | QuestionNotFoundException e) {
            return notFound();
        }
    }

    @GetMapping("/top3")
    public Object getTop3Ranking() {
        return ok(userService.getTop3Leaderboard());
    }

    @PostMapping("/rank")
    public Object getRanking(@RequestBody UserIDDTO userIDDTO) {
        User user = userService.findUserById(Long.parseLong(userIDDTO.getUserId())).orElse(null);
        if (user == null) {
            return notFound();
        }
        try {
            return ok(userService.getRanking(user.getId()));
        } catch (Exception e) {
            return fail(null, e.getMessage());
        }
    }

}