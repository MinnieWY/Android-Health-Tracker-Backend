package com.wyminnie.healthtracker.base.user;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User findByUsername(String username);

    User findByUserId(Long userId);

    Optional<UserDTO> createUser(UserRegistrationDto registrationDto)
            throws DuplicateUsernameException, UserValidException;

    boolean authorizeAccess();

    boolean verifyUserCredentials(String username, String password);

    void saveOrUpdate(User user);

    Optional<UserDTO> findUserDTOById(Long userId);

    Optional<User> findUserById(Long userId);

    public List<UserListItemDTO> searchUsers(String query);
}
