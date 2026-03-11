package com.example.Boggle.Service;


import com.example.Boggle.Model.Tables.Game;
import com.example.Boggle.Model.Tables.GameStatus;
import com.example.Boggle.repository.FoundWordRepository;
import com.example.Boggle.repository.GameRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service responsible for computing player scores and determining the winner
 * of a game based on the point values of words found during play.
 * Class has 2 functions
 * 1. Compute the current score totals while the game is in progress.
 * 2. Finalize the game and record the winner.
 */
@Service
public class GameScoreService {
    /**
     * Data structure for point storing.
     * Holds the score totals for each player and the ID of the winning player.
     */
    public static class Totals{
        public Integer player1Id;
        public Integer player2Id;
        public int player1Points;
        public Integer player2Points;
        public Integer winnerPlayerId;
    }

    private final GameRepository gameRepository;
    private final FoundWordRepository foundWordRepository;

    /**
     * Constructs a GameScoreService with the repositories needed
     * to load games and calculate player scores.
     */
    public GameScoreService(GameRepository gameRepository,
                              FoundWordRepository foundWordRepository){
        this.gameRepository = gameRepository;
        this.foundWordRepository = foundWordRepository;
    }

    /**
     * Computes the current score totals for a game.
     *
     * @param gameId the ID of the game; may refer to a SOLO, BOT, or MULTIPLAYER game
     * @return a Totals object containing both player IDs, their scores, and the current winner
     */
    @Transactional()
    public Totals computeTotals(Integer gameId){
        Game game = gameRepository.findById(gameId).orElseThrow();
        Integer p1Id = game.getPlayer1().getId();
        Integer p2Id = (game.getPlayer2()==null)? null: game.getPlayer2().getId();

        //Loads points from foundWordRepository of players
        int p1Pts = foundWordRepository.totalPointsForPlayer(gameId,p1Id);
        Integer p2Pts = (p2Id==null)? null: foundWordRepository.totalPointsForPlayer(gameId,p2Id);

        Totals totalOut = new Totals();
        totalOut.player1Id = p1Id;
        totalOut.player2Id = p2Id;
        totalOut.player1Points = p1Pts;
        totalOut.player2Points = p2Pts;

        if(p2Pts == null){
            totalOut.winnerPlayerId = p1Id;
        }else if(p1Pts > p2Pts){
            totalOut.winnerPlayerId = p1Id;
        }else if(p2Pts > p1Pts){
            totalOut.winnerPlayerId = p2Id;
        }else{
            totalOut.winnerPlayerId = null;
        }
        return totalOut;
    }

    /**
     * Finalizes the game, sets its status to FINISHED, records the completion time,
     * and stores the winning player based on the final score totals.
     *
     * @param gameId the ID of the game; may refer to a SOLO, BOT, or MULTIPLAYER game
     * @return a Totals object containing both player IDs, their scores, and the current winner
     */
    @Transactional
    public Totals finishGame(Integer gameId) {
        Game game = gameRepository.findById(gameId).orElseThrow();
        Totals totals = computeTotals(gameId);

        game.setStatus(GameStatus.FINISHED);
        game.setFinishedAt(LocalDateTime.now());

        if (totals.winnerPlayerId == null) {
            game.setWinner(null);
        } else if (game.getPlayer1().getId().equals(totals.winnerPlayerId)) {
            game.setWinner(game.getPlayer1());
        } else if (game.getPlayer2() != null && game.getPlayer2().getId().equals(totals.winnerPlayerId)) {
            game.setWinner(game.getPlayer2());
        }

        gameRepository.save(game);
        return totals;
    }


}
