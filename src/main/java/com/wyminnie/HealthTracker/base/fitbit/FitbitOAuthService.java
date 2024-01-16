package com.wyminnie.healthtracker.base.fitbit;

import com.github.scribejava.apis.FitbitApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FitbitOAuthService {
    // @Value("${fitbit.clientId}")
    // private String clientId;
    // @Value("${fitbit.clientSecret}")
    // private String clientSecret;
    // @Value("${fitbit.callbackUrl}")
    // private String callbackUrl;

    private static final String CLIENT_ID = "23R9K4";
    private static final String CLIENT_SECRET = "c6495d349968c1b382792960fa32f256";
    private static final String CALLBACK_URL = "https://364f-161-81-184-27.ngrok-free.app/callback";

    public String getAuthorizationUrl(Long userId) {
        OAuth20Service service = getOAuthService();

        return service.createAuthorizationUrlBuilder()
                .state(Long.toString(0))
                .build();
    }

    public OAuth20Service getOAuthService() {
        return new ServiceBuilder(CLIENT_ID)
                .apiSecret(CLIENT_SECRET)
                .callback(CALLBACK_URL)
                .defaultScope("activity profile")
                .defaultScope("heartrate")
                .responseType("code")
                .build(FitbitApi20.instance());
    }

}