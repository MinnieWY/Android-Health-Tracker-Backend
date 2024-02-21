package com.wyminnie.healthtracker.base.user;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User findByUsername(String username);

    User findByUserId(Long userId);

    Optional<UserDTO> createUser(UserRegistrationDto registrationDto)
            throws DuplicateUsernameException, UserValidException;

    boolean authorizeAccess();

    boolean verifyUserCredentials(User user, String password);

    void saveOrUpdate(User user);

    Optional<UserDTO> findUserById(Long userId);

    Optional<User> findUserById(Long userId);

    public List<UserListItemDTO> searchUsers(String query);
    
    boolean updatePreference(User user, String preference);

    public UserDTO updateAccessToken(User user);
}
