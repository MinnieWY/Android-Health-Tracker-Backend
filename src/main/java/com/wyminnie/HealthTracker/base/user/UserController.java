package com.wyminnie.healthtracker.base.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.scribejava.apis.FitbitApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.wyminnie.healthtracker.base.fitbit.FitbitOAuthService;
import com.wyminnie.healthtracker.common.ControllerUtils;

import static com.wyminnie.healthtracker.common.ControllerUtils.ok;
import static com.wyminnie.healthtracker.common.ControllerUtils.duplicatedUsername;
import static com.wyminnie.healthtracker.common.ControllerUtils.notFound;
import static com.wyminnie.healthtracker.common.ControllerUtils.serverError;
import static com.wyminnie.healthtracker.common.ControllerUtils.userValid;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private FitbitOAuthService fitbitOAuthService;

    @PostMapping("/register")
    public ResponseEntity<String> registration(@RequestBody UserDTO dto) {
        // try {
        // return userService.createUser(dto).map(ControllerUtils::ok);
        // } catch (DuplicateUsernameException e) {
        // return duplicatedUsername();
        // } catch (UserValidException e) {
        // return userValid();
        // } catch (Exception e) {
        // return serverError();
        // }
        // Optional<UserDTO> user = userService.createUser(dto);
        // return ResponseEntity.status(HttpStatus.CREATED).build();

        // Create a new user(makeup for now)
        User user = userService.findByUsername("admin");

        // Initiate the Fitbit OAuth flow
        String authorizationUrl = fitbitOAuthService.getAuthorizationUrl(user.getId());

        // Construct the response string with the authorization URL and user ID
        String response = authorizationUrl + "," + user.getId();

        // Return the response string to the frontend
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/callback")
    public String handleCallback(@RequestParam("code") String authorizationCode,
            @RequestParam("state") String state, Model model) {

        OAuth20Service service = fitbitOAuthService.getOAuthService();

        // Exchange the authorization code for an access token
        try {
            OAuth2AccessToken accessToken = service.getAccessToken(authorizationCode);
            String fitbitAccessToken = accessToken.getAccessToken();
            String fitbitRefreashToken = accessToken.getRefreshToken();

            // Get the currently logged in user
            User user = userService.findByUsername("admin");

            // Update the user's access token
            user.setAccessToken(fitbitAccessToken);
            user.setRefreashToken(fitbitRefreashToken);
            userService.saveOrUpdate(user);

            String htmlResponse = "<html>"
                    + "<head>"
                    + "<title>Fitbit Authorization Complete</title>"
                    + "</head>"
                    + "<body>"
                    + "<h1>Fitbit Authorization Complete</h1>"
                    + "<p>The authorization process is complete.</p>"
                    + "<a href=\"ht2024://login\">Click here</a> to redirect back to the application."
                    + "</body>"
                    + "</html>";

            return htmlResponse;

        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return "failure";
    }

    @GetMapping("/{userId}")
    public Optional<UserDTO> getUserById(@PathVariable String userId) {
        // Fetch the user information from the database using the userId
        return userService.findUserById(Long.valueOf(userId));
    }
}
