package com.wyminnie.healthtracker.base.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wyminnie.healthtracker.common.ControllerUtils;

@RestController
public class LoginController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Object login(@RequestBody UserLoginDto userLoginDto) {

        User loginUser = userService.findByUsername(userLoginDto.getUsername()).orElse(null);

        if (loginUser == null) {
            return ControllerUtils.passwordMismatched();
        } else {
            if (userService.verifyUserCredentials(loginUser,
                    userLoginDto.getPassword())) {
                return ControllerUtils.ok(userService.updateAccessToken(loginUser));
            } else {
                return ControllerUtils.passwordMismatched();
            }
        }
    }

}
