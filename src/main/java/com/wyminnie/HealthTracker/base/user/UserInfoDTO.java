package com.wyminnie.healthtracker.base.user;

import lombok.Data;

@Data
public class UserInfoDTO {
    private Long id;
    private String username;
    private String email;
    private String gender;
    private String height;
    private String weight;
    private String preference;

    public static UserInfoDTO from(User entity) {
        UserInfoDTO dto = new UserInfoDTO();
        dto.id = entity.getId();
        dto.username = entity.getUsername();
        dto.email = entity.getEmail();
        dto.gender = entity.getGender();
        dto.height = Float.toString(entity.getHeight());
        dto.weight = Float.toString(entity.getWeight());
        dto.preference = entity.getPreference();

        return dto;
    }
}
