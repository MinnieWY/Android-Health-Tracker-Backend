package com.wyminnie.healthtracker.base.acm.user;

import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);
}
