package com.wyminnie.healthtracker.base.community;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
        // @Query("""
        // SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END \
        // FROM Friendship f \
        // WHERE :user1 MEMBER OF f.users AND :user2 MEMBER OF f.users\
        // """)
        // boolean existsByUsers(@Param("user1") String currentUser, @Param("user2")
        // String targetUser);

        @Query("""
                        SELECT f.user1 as userId, MAX(f.score) as point \
                        FROM Friend f \
                        WHERE f.user1 = :currentUser OR f.user2 = :currentUser \
                        GROUP BY f.user1 \
                        ORDER BY point DESC \
                        """)
        List<LeaderboardDTO> findTop3FriendsWithScores(@Param("currentUser") Long currentUser);

}