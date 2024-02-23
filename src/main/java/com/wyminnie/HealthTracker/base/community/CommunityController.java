package com.wyminnie.healthtracker.base.community;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wyminnie.healthtracker.base.user.User;
import com.wyminnie.healthtracker.base.user.UserDTO;
import com.wyminnie.healthtracker.base.user.UserListItemDTO;
import com.wyminnie.healthtracker.base.user.UserService;

import java.util.List;
import java.util.Optional;

@RestController
public class CommunityController {
    @Autowired
    private UserService userService;

    @Autowired
    private CommunityService communityService;

    @GetMapping("/list")
    public List<UserListItemDTO> searchUsers(@RequestParam String query) {
        return userService.searchUsers(query);
    }

    @GetMapping("/search")
    public FriendDTO getUserProfile(@RequestParam String currentUserId, @RequestParam String targetid) {
        Optional<User> currentUser = userService.findUserById(Long.parseLong(currentUserId));
        Optional<User> friend = userService.findUserById(Long.parseLong(targetid));
        if (currentUser.isEmpty() || friend.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        FriendDTO friendDTO = new FriendDTO();
        if (communityService.isFriend(currentUser, friend)) {
            friendDTO.setFriend(true);
        } else {
            friendDTO.setFriend(false);
        }
        return friendDTO;
    }

    @GetMapping("addFriend")
    public ResponseEntity<String> addFriendRequest(@RequestParam String currentUserId, @RequestParam String friendId)
            throws Exception {
        Optional<User> currentUser = userService.findUserById(Long.parseLong(currentUserId));
        Optional<User> targetUser = userService.findUserById(Long.parseLong(friendId));

        if (currentUser.isEmpty() || targetUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (!communityService.addFriendRequest(currentUser, targetUser)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }


}