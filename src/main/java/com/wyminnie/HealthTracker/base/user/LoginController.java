package com.wyminnie.healthtracker.base.user;

import static com.wyminnie.healthtracker.common.ControllerUtils.notFound;
import static com.wyminnie.healthtracker.common.ControllerUtils.serverError;
import static com.wyminnie.healthtracker.common.ControllerUtils.ok;
import static com.wyminnie.healthtracker.common.ControllerUtils.passwordMismatched;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Object login(@RequestBody UserLoginDto userLoginDto) {

        User loginUser = userService.findByUsername(userLoginDto.getUsername()).orElse(null);

        if (loginUser == null) {
            return passwordMismatched();
        } else {
            if (userService.verifyUserCredentials(loginUser,
                    userLoginDto.getPassword())) {
                return ok(userService.updateAccessToken(loginUser));
            } else {
                return passwordMismatched();
            }
        }
    }

    @PostMapping("/forget-password")
    public Object handleResetPassword(@RequestBody ForgetPasswordDTO forgetPasswordDTO) {
        User requestUser = userService.findByEmail(forgetPasswordDTO.getEmail()).orElse(null);

        if (requestUser == null) {
            return notFound();
        }
        try {
            User renewedUser = userService.resetPassword(requestUser, forgetPasswordDTO.getNewPassword());
            return ok(userService.updateAccessToken(renewedUser));
        } catch (Exception e) {
            return serverError();
        }
    }

}
