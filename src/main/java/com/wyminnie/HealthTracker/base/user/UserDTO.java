package com.wyminnie.healthtracker.base.user;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String prefernce;
    private int point

    public static UserDTO from(User entity) {
        UserDTO dto = new UserDTO();
        dto.id = entity.getId();
        dto.username = entity.getUsername();
        dto.email = entity.getEmail();
        if (entity.getPreference() != null) {
            dto.prefernce = entity.getPreference().trim();
        } else {
            dto.prefernce = entity.getPreference();
        }
        dto.point = entity.getPoint();
        return dto;
    }
}
