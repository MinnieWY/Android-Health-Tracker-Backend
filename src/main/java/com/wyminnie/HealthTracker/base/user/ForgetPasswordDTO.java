package com.wyminnie.healthtracker.base.user;

import lombok.Data;

@Data
public class ForgetPasswordDTO {
    private String email;
    private String newPassword;
}
