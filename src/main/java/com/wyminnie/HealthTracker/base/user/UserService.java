package com.wyminnie.healthtracker.base.user;

import java.util.Optional;

public interface UserService {

    User findByUsername(String username);

    Optional<UserDTO> createUser(UserRegistrationDto registrationDto)
            throws DuplicateUsernameException, UserValidException;

    boolean authorizeAccess();

    boolean verifyUserCredentials(String username, String password);

    void saveOrUpdate(User user);

    Optional<UserDTO> findUserById(Long userId);
}
