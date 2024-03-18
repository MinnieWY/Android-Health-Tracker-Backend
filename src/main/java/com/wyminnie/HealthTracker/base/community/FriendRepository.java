package com.wyminnie.healthtracker.base.community;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wyminnie.healthtracker.base.user.User;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    // @Query("""
    // SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END \
    // FROM Friendship f \
    // WHERE :user1 MEMBER OF f.users AND :user2 MEMBER OF f.users\
    // """)
    // boolean existsByUsers(@Param("user1") Optional<User> currentUser,
    // @Param("user2") Optional<User> targetUser);
}