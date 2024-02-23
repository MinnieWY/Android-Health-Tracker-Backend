package com.wyminnie.healthtracker.base.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Override
    public Optional<UserDTO> createUser(UserRegistrationDto userRegistrationDto)
            throws DuplicateUsernameException, UserValidException {
        User duplicateUsernameUser = findByUsername(userRegistrationDto.getUsername());

        if (duplicateUsernameUser != null) {
            throw new DuplicateUsernameException();
        }

        if (StringUtils.hasText(userRegistrationDto.getUsername())
                || StringUtils.hasText(userRegistrationDto.getEmail()) || StringUtils.hasText(
                        userRegistrationDto.getPassword())) {
            throw new UserValidException();
        }

        User entity = new User();
        entity.setUsername(userRegistrationDto.getUsername());
        entity.setEmail(userRegistrationDto.getEmail());
        entity.setPassword(userRegistrationDto.getPassword());

        final User finalEntity = userRepository.saveAndFlush(entity);

        return findUserDTOById(finalEntity.getId());
    }

    @Override
    public boolean authorizeAccess() {
        return true;
    }

    @Override
    public boolean verifyUserCredentials(User user, String password) {
        if (user.getPassword().equals(password)) {
            return true;
        } else {
            return false;
        }
    }

    public Optional<UserDTO> findUserDTOById(Long id) {
        return userRepository.findById(id).map(u -> {

            UserDTO dto = new UserDTO();
            dto = UserDTO.from(u);
            return dto;
        });
    }

    @Override
    public Optional<User> findUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return Optional.empty();
        } else {
            return Optional.of(user);
        }
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findByUserId(Long id) {
        return userRepository.findOneById(id);
    }

    @Override
    public void saveOrUpdate(User user) {
        userRepository.save(user);
    }

    public List<UserListItemDTO> searchUsers(String query) {
        if (!StringUtils.hasText(query)) {
            return Collections.emptyList();
        }
        List<User> searchResults = userRepository.findByUsernameContaining(query);
        return searchResults.stream()
                .map(UserListItemDTO::from)
                .collect(Collectors.toList());
    }

    @Override
    public boolean updatePreference(User user, String preference) {
        user.setPreference(preference);
        userRepository.save(user);
        return true;
    }

    @Override
    public UserDTO updateAccessToken(User user) {

        Mono<FitbitRefreash> refreashTokenMono = getFitbitRefreashToken(user.getRefreashToken());
        FitbitRefreash refreashData = refreashTokenMono.block();
        user.setAccessToken(refreashData.getAccess_token());
        user.setRefreashToken(refreashData.getRefresh_token());
        User savedUser = userRepository.saveAndFlush(user);
        return UserDTO.from(savedUser);
    }

    private Mono<FitbitRefreash> getFitbitRefreashToken(String refreshToken) {
        WebClient webClient = WebClient.create("https://api.fitbit.com/oauth2/token");
        return webClient.post().header("Content-Type", "application/json")
                .body(BodyInserters.fromFormData("grant_type", "refresh_token")
                        .with("refresh_token", refreshToken).with("client_id", "23R9K4"))
                .retrieve().bodyToMono(FitbitRefreash.class);
    }
}