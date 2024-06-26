package com.wyminnie.healthtracker.base.user;

import java.util.List;
import java.util.Optional;

public interface UserService {

        Optional<User> findByUsername(String username);

        User findByUserId(Long userId);

        Optional<UserDTO> findUserDTOById(Long userId);

        Optional<UserInfoDTO> getUserInfo(Long userId);

        Optional<UserDTO> createUser(UserRegistrationDto registrationDto)
                        throws DuplicateUsernameException, UserValidException;

        boolean authorizeAccess();

        Optional<UserDTO> editUser(UserInfoDTO userDTO)
                        throws DuplicateUsernameException, UserValidException, Exception;

        boolean verifyUserCredentials(User user, String password);

        void saveOrUpdate(User user); // For Fitbit OAuth

        Optional<User> findUserById(Long userId);

        public List<UserListItemDTO> searchUsers(String query);

        public UserDTO updateAccessToken(User user);

        boolean changePassword(ChangePasswordRequestDTO changePasswordRequestDTO)
                        throws PasswordMismatchedException, UserNotFoundException;

        Optional<User> findByEmail(String email);

        User resetPassword(User user, String newPassword);
}
