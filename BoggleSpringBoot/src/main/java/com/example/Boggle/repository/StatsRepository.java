package com.example.Boggle.repository;

import com.example.Boggle.Model.Tables;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface StatsRepository extends JpaRepository<User, Integer> {
    interface UserStatsProjection {
        Long getGamesPlayed();
        Long getGamesWon();
        Long getWordsFound();
    }

    @Query(value = "SELECT " +
            "(SELECT COUNT(*) FROM games WHERE player1_id = :userId OR player2_id = :userId) as gamesPlayed, " +
            "(SELECT COUNT(*) FROM games WHERE winner_player_id = :userId) as gamesWon, " +
            "(SELECT COUNT(*) FROM found_words WHERE player_id = :userId) as wordsFound",
            nativeQuery = true)
    UserStatsProjection getUserStats(@Param("userId") Integer userId);
}