package com.wyminnie.healthtracker.base.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user")
public class User {
    @Id
    protected Long id;
    private String username;
    private String email;
    private String userPW;
    private String accessToken;
    private String refreashToken;
    private String preference;

}
