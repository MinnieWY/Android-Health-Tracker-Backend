package com.wyminnie.healthtracker.base.user;

import lombok.Getter;

@Getter
public class ChangePasswordRequestDTO {
    private String userId;
    private String oldPassword;
    private String newPassword;
}
