package com.wyminnie.healthtracker.base.community;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FriendDTO {
    private Long id;
    private Long userId;
    private Long friendId;
    private boolean isFriend;
}
