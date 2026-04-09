package com.example.Boggle.repository;

import com.example.Boggle.Model.Tables;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface StatsRepository extends JpaRepository<User, Integer> {
    @Query("SELECT new com.example.dto.UserStatsDTO(" +
            // Games Played: Count where user is player1 OR player2
            "(SELECT COUNT(g) FROM Game g WHERE g.player1.id = :userId OR g.player2.id = :userId), " +

            // Games Won: Count where user is the winner
            "(SELECT COUNT(g) FROM Game g WHERE g.winnerPlayerId = :userId), " +

            // Words Found: Count entries in found_words
            "(SELECT COUNT(fw) FROM FoundWord fw WHERE fw.player.id = :userId))")
    UserStatsDTO getUserStats(@Param("userId") Integer userId);
}