package com.wyminnie.healthtracker.base.user;

import java.util.Optional;

public interface UserService {

    Optional<User> findByUsername(String username);

    User findByUserId(Long userId);

    Optional<UserDTO> findUserDTOById(Long userId);

    Optional<UserInfoDTO> getUserInfo(Long userId);

    Optional<UserDTO> createUser(UserRegistrationDto registrationDto)
            throws DuplicateUsernameException, UserValidException;

    boolean authorizeAccess();

    Optional<UserDTO> editUser(UserInfoDTO userDTO) throws DuplicateUsernameException, UserValidException, Exception;

    boolean verifyUserCredentials(User user, String password);

    void saveOrUpdate(User user);

    public UserDTO updateAccessToken(User user);

    void changePassword(ChangePasswordRequestDTO changePasswordRequestDTO)
            throws PasswordMismatchedException, UserNotFoundException;
}
