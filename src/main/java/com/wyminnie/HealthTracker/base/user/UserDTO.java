package com.wyminnie.healthtracker.base.user;

import java.util.ArrayList;
import java.util.stream.Collectors;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String prefernce;

    public static UserDTO from(User entity) {
        UserDTO dto = new UserDTO();
        dto.id = entity.getId();
        dto.username = entity.getUsername();
        dto.email = entity.getEmail();
        dto.prefernce = entity.getPrefernce();

        return dto;
    }
}
