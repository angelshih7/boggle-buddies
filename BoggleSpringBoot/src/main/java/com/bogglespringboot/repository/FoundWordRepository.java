package com.bogglespringboot.repository;

import com.bogglespringboot.Model.Tables.FoundWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FoundWordRepository extends JpaRepository<FoundWord, Integer> {

    // handy for "show all words found in a game"
    List<FoundWord> findByGame_Id(Integer gameId);

    // handy for "show a player's words in a game"
    List<FoundWord> findByGame_IdAndPlayer_Id(Integer gameId, Integer playerId);

    @Query("""
    select coalesce(sum(f.dictionaryWord.pointValue), 0)
   from FoundWord f
   where f.game.id = :gameId and f.player.id = :playerId
    """)
    Integer totalPointsForPlayer(@Param("gameId") Integer gameId,
                                 @Param("playerId") Integer playerId);
    // handy for quick duplicate check
    boolean existsByGame_IdAndPlayer_IdAndDictionaryWord_Id(Integer gameId, Integer playerId, Integer dictionaryWordId);
}
