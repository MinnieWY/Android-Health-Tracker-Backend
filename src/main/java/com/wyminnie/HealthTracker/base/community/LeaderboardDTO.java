package com.wyminnie.healthtracker.base.community;

import com.wyminnie.healthtracker.base.user.User;
import lombok.Data;

@Data
public class LeaderboardDTO {
    private Long userId;
    private String username;
    private int point;

    public static LeaderboardDTO from(User entity) {
        LeaderboardDTO dto = new LeaderboardDTO();
        dto.setUserId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setPoint(entity.getPoint());
        return dto;
    }

}
