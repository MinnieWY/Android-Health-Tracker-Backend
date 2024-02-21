package com.wyminnie.healthtracker.base.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
        entity.setUserPW(userRegistrationDto.getPassword());

        final User finalEntity = userRepository.save(entity);

        return findUserById(finalEntity.getId());
    }

    @Override
    public boolean authorizeAccess() {
        return true;
    }

    @Override
    public boolean verifyUserCredentials(String username, String password) {
        // Long userId = findByUsername(username).get().getId();

        // return findByUsername(username)
        // .filter(user -> user.isDataStatusActive())
        // .filter(user -> {
        // boolean matched = encoder.matches(password, user.getPassword());

        // if (!matched) {
        // userService.increaseFailAttempt(user.getId());
        // }

        // return matched;
        // })
        // .map(user -> {
        // userService.resetFailAttempt(user.getId());

        // });
        return true;
    }

    public Optional<UserDTO> findUserById(Long id) {
        return userRepository.findById(id).map(u -> {

            UserDTO dto = new UserDTO();
            dto = UserDTO.from(u);
            return dto;
        });
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

    @Override
    public boolean updatePreference(User user, String preference) {
        user.setPreference(preference);
        userRepository.save(user);
        return true;
    }
}