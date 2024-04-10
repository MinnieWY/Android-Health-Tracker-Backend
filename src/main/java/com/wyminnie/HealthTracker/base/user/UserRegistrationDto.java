package com.wyminnie.healthtracker.base.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationDto {
    private String username;
    private String gender;
    private String email;
    private String password;
}