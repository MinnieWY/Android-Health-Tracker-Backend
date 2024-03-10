package com.wyminnie.healthtracker.base.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.wyminnie.healthtracker.base.fitbit.FitbitOAuthService;

import static com.wyminnie.healthtracker.common.ControllerUtils.ok;
import static com.wyminnie.healthtracker.common.ControllerUtils.passwordMismatched;
import static com.wyminnie.healthtracker.common.ControllerUtils.permissionDenied;
import static com.wyminnie.healthtracker.common.ControllerUtils.duplicatedUsername;
import static com.wyminnie.healthtracker.common.ControllerUtils.notFound;
import static com.wyminnie.healthtracker.common.ControllerUtils.serverError;
import static com.wyminnie.healthtracker.common.ControllerUtils.userNotFound;
import static com.wyminnie.healthtracker.common.ControllerUtils.userValid;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private FitbitOAuthService fitbitOAuthService;

    @PostMapping("/register")
    public Object registration(@RequestBody UserRegistrationDto dto) {
        try {

            UserDTO userDTO = userService.createUser(dto).orElse(null);
            // Initiate the Fitbit OAuth flow
            String authorizationUrl = fitbitOAuthService.getAuthorizationUrl(userDTO.getId());

            return ok(userDTO, authorizationUrl);
        } catch (DuplicateUsernameException e) {
            return duplicatedUsername();
        } catch (UserValidException e) {
            return userValid();
        } catch (Exception e) {
            return serverError();
        }

    }

    @GetMapping("/callback")
    public Object handleCallback(@RequestParam("code") String authorizationCode,
            @RequestParam("state") String state, Model model) {

        OAuth20Service service = fitbitOAuthService.getOAuthService();

        // Exchange the authorization code for an access token
        try {
            OAuth2AccessToken accessToken = service.getAccessToken(authorizationCode);
            String fitbitAccessToken = accessToken.getAccessToken();
            String fitbitRefreashToken = accessToken.getRefreshToken();

            // Get the currently logged in user
            User user = userService.findByUserId(Long.valueOf(state));

            // Update the user's access token
            user.setAccessToken(fitbitAccessToken);
            user.setRefreashToken(fitbitRefreashToken);
            userService.saveOrUpdate(user);

            String htmlResponse = """
                    <html>\
                    <head>\
                    <title>Fitbit Authorization Complete</title>\
                    </head>\
                    <body>\
                    <h1>Fitbit Authorization Complete</h1>\
                    <p>The authorization process is complete.</p>\
                    <a href="ht2024://login">Click here</a> to redirect back to the application.\
                    </body>\
                    </html>\
                    """;

            return ok(htmlResponse);
        } catch (Exception e) {
            return serverError();
        }
    }

    @GetMapping("/{userId}")
    public Object getUserGerneralInfo(@PathVariable("userId") String userId) {
        return userService.findUserDTOById(Long.valueOf(userId)).map(u -> {
            return ok(u);
        }).orElse(notFound());
    }

    @GetMapping("/userInfo/{userId}")
    public Object getUserInfo(@PathVariable("userId") String userId) {
        return userService.getUserInfo(Long.valueOf(userId)).map(u -> {
            return ok(u);
        }).orElse(notFound());
    }

    @PostMapping("/changePassword")
    public Object changePassword(@RequestBody ChangePasswordRequestDTO dto) {
        try {
            userService.changePassword(dto);
            return ok(true);
        } catch (PasswordMismatchedException e) {
            return passwordMismatched();
        } catch (UserNotFoundException e) {
            return userNotFound();
        }
    }

    @PostMapping("/editUser")
    public Object editUser(@RequestBody UserInfoDTO userDTO) {
        try {
            return userService.editUser(userDTO).map(u -> {
                return ok(u);
            });
        } catch (DuplicateUsernameException e) {
            return duplicatedUsername();
        } catch (UserValidException e) {
            return userValid();
        } catch (Exception e) {
            return serverError();
        }
    }

}
