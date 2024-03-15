package com.wyminnie.healthtracker.base.user;

import java.util.Objects;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.wyminnie.healthtracker.base.community.LeaderboardDTO;
import com.wyminnie.healthtracker.common.ServerErrorException;

import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Override
    public Optional<UserDTO> createUser(UserRegistrationDto userRegistrationDto)
            throws DuplicateUsernameException, UserValidException {
        User duplicateUsernameUser = findByUsername(userRegistrationDto.getUsername()).orElse(null);

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

    @Override
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<UserDTO> findUserDTOById(Long id) {
        return userRepository.findById(id).map(u -> {
            UserDTO dto = new UserDTO();
            dto = UserDTO.from(u);
            return dto;
        });
    }

    @Override
    public Optional<UserInfoDTO> getUserInfo(Long userId) {
        return userRepository.findById(userId).map(u -> {
            UserInfoDTO dto = new UserInfoDTO();
            dto = UserInfoDTO.from(u);
            return dto;
        });
    }

    @Override
    public Optional<User> findByUsername(String username) {
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
    public UserDTO updateAccessToken(User user) throws ServerErrorException {

        Mono<FitbitRefreash> refreashTokenMono = getFitbitRefreashToken(user.getRefreashToken());
        FitbitRefreash refreashData = refreashTokenMono.block();

        if (refreashData == null) {
            throw new ServerErrorException();
        }

        user.setAccessToken(refreashData.getAccess_token());
        user.setRefreashToken(refreashData.getRefresh_token());
        User savedUser = userRepository.saveAndFlush(user);
        UserDTO u = UserDTO.from(savedUser);
        return u;
    }

    private Mono<FitbitRefreash> getFitbitRefreashToken(String refreshToken) {
        WebClient webClient = WebClient.create("https://api.fitbit.com/oauth2/token");
        return webClient.post().header("Content-Type", "application/json")
                .body(BodyInserters.fromFormData("grant_type", "refresh_token")
                        .with("refresh_token", refreshToken).with("client_id", "23R9K4"))
                .retrieve().bodyToMono(FitbitRefreash.class);
    }

    @Override
    public void changePassword(ChangePasswordRequestDTO changePasswordRequestDTO)
            throws PasswordMismatchedException, UserNotFoundException {
        User user = userRepository.findById(Long.valueOf(changePasswordRequestDTO.getUserId())).orElse(null);
        if (user == null) {
            throw new UserNotFoundException();
        }
        if (verifyUserCredentials(user, changePasswordRequestDTO.getOldPassword())) {
            throw new PasswordMismatchedException();
        }
        user.setPassword(changePasswordRequestDTO.getNewPassword());
        userRepository.save(user);
    }

    @Override
    public Optional<UserDTO> editUser(UserInfoDTO userDTO)
            throws DuplicateUsernameException, UserValidException, Exception {
        if (StringUtils.hasText(userDTO.getUsername()) || StringUtils.hasText(userDTO.getEmail())
                || StringUtils.hasText(userDTO.getPreference()) || StringUtils.hasText(userDTO.getGender())
                || StringUtils.hasText(userDTO.getHeight()) || StringUtils.hasText(userDTO.getWeight())) {
            throw new UserValidException();
        }

        User duplicatedUsernameUser = findByUsername(userDTO.getUsername()).orElse(null);
        if (duplicatedUsernameUser != null &&
                !Objects.equals(duplicatedUsernameUser.getId(), userDTO.getId())) {
            throw new DuplicateUsernameException();
        }

        return userRepository.findById(userDTO.getId()).map(entity -> {
            entity.setUsername(userDTO.getUsername());
            entity.setEmail(userDTO.getEmail());
            entity.setGender(userDTO.getGender());
            entity.setHeight(Float.valueOf(userDTO.getHeight()));
            entity.setWeight(Float.valueOf(userDTO.getWeight()));
            entity.setPreference(userDTO.getPreference());

            return UserDTO.from(userRepository.saveAndFlush(entity));
        });

    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserDTO resetPassword(User user, String newPassword) {
        user.setPassword(newPassword);
        User savedUser = userRepository.saveAndFlush(user);
        return UserDTO.from(savedUser);
    }

    @Override
    public List<LeaderboardDTO> getLeaderboard() {
        return userRepository.findtop3UsersByOrderByPointsDesc().stream()
                .map(LeaderboardDTO::from).collect(Collectors.toList());
    }

    @Override
    public Optional<UserInfoDTO> getUserPublicProfile(Long userId) {
        return userRepository.findById(userId).map(u -> {
            UserInfoDTO dto = new UserInfoDTO();
            dto = UserInfoDTO.from(u);
            return dto;
        });
    }
}