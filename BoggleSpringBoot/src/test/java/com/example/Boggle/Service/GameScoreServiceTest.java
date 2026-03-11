package com.example.Boggle.Service;

import com.example.Boggle.Model.Tables.Game;
import com.example.Boggle.Model.Tables.GameStatus;
import com.example.Boggle.Model.Tables.User;
import com.example.Boggle.repository.FoundWordRepository;
import com.example.Boggle.repository.GameRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameScoreServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private FoundWordRepository foundWordRepository;

    @InjectMocks
    private GameScoreService gameScoreService;

    @Test
    void computeTotalsReturnsCorrectScoresAndWinnerWhenPlayerOneWins() {
        User player1 = new User("p1", "p1@test.com", "hash");
        User player2 = new User("p2", "p2@test.com", "hash");
        Game game = new Game();
        game.setPlayer1(player1);
        game.setPlayer2(player2);

        setPrivateId(player1, 1);
        setPrivateId(player2, 2);
        game.setId(10);

        when(gameRepository.findById(10)).thenReturn(Optional.of(game));
        when(foundWordRepository.totalPointsForPlayer(10, 1)).thenReturn(8);
        when(foundWordRepository.totalPointsForPlayer(10, 2)).thenReturn(5);

        GameScoreService.Totals totals = gameScoreService.computeTotals(10);

        assertEquals(1, totals.player1Id);
        assertEquals(2, totals.player2Id);
        assertEquals(8, totals.player1Points);
        assertEquals(5, totals.player2Points);
        assertEquals(1, totals.winnerPlayerId);
    }

    @Test
    void finishGameSetsStatusFinishedAndWinner() {
        User player1 = new User("p1", "p1@test.com", "hash");
        User player2 = new User("p2", "p2@test.com", "hash");
        Game game = new Game();
        game.setPlayer1(player1);
        game.setPlayer2(player2);

        setPrivateId(player1, 1);
        setPrivateId(player2, 2);
        game.setId(22);

        when(gameRepository.findById(22)).thenReturn(Optional.of(game));
        when(foundWordRepository.totalPointsForPlayer(22, 1)).thenReturn(4);
        when(foundWordRepository.totalPointsForPlayer(22, 2)).thenReturn(7);
        when(gameRepository.save(any(Game.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GameScoreService.Totals totals = gameScoreService.finishGame(22);

        assertNotNull(totals);
        assertEquals(GameStatus.FINISHED, game.getStatus());
        assertNotNull(game.getFinishedAt());
        assertEquals(player2, game.getWinner());
    }

    private static void setPrivateId(User user, Integer id) {
        try {
            var field = User.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(user, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}