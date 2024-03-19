package com.wyminnie.healthtracker.base.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    List<User> findByUsernameContaining(String query);

    User findOneById(Long id);

    Optional<User> findByEmail(String email);

    @Query(nativeQuery = true, value = "SELECT ranking FROM (SELECT id, ROW_NUMBER() OVER (ORDER BY point DESC) AS ranking FROM user) AS ranked_users WHERE id = :userId")
    Integer getRanking(@Param("userId") Long id);

    List<User> findTop3ByOrderByPointDesc();
}