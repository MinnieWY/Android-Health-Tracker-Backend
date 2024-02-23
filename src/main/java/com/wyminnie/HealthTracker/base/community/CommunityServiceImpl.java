package com.wyminnie.healthtracker.base.community;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wyminnie.healthtracker.base.user.User;

@Service
public class CommunityServiceImpl implements CommunityService {
    @Autowired
    FriendRepository friendRepository;
    @Autowired
    QuestionRepository questionRepository;

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

}
