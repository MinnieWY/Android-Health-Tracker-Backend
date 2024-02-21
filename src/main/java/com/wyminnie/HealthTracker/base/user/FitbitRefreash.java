package com.wyminnie.healthtracker.base.user;

import lombok.Getter;

@Getter
public class FitbitRefreash {
    private String access_token;
    private int expires_in;
    private String refresh_token;
    private String token_type;
    private String user_id;
}
