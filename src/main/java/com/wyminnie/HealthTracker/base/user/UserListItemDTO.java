package com.wyminnie.healthtracker.base.user;

import lombok.Data;

@Data
public class UserListItemDTO {
    private Long id;
    private String username;
    private String email;

    public static UserListItemDTO from(User user) {
        UserListItemDTO dto = new UserListItemDTO();
        dto.id = user.getId();
        dto.username = user.getUsername();
        dto.email = user.getEmail();
        return dto;
    }
}