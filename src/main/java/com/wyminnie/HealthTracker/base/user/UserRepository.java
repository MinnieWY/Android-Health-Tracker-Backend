package com.wyminnie.healthtracker.base.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    List<User> findByUsernameContaining(String query);

    User findOneById(Long id);

    Optional<User> findByEmail(String email);

    List<User> findtop3UsersByOrderByPointsDesc();
}