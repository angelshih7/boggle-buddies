package com.bogglespringboot.Service;


import com.bogglespringboot.Model.Tables.Game;
import com.bogglespringboot.Model.Tables.GameStatus;
import com.bogglespringboot.repository.FoundWordRepository;
import com.bogglespringboot.repository.GameRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class GameScoreService {
    public static class Totals{
        public Integer player1Id;
        public Integer player2Id;
        public int player1Points;
        public Integer player2Points;
        public Integer winnerPlayerId;
    }

    private final GameRepository gameRepository;
    private final FoundWordRepository foundWordRepository;

    public GameScoreService(GameRepository gameRepository,
                              FoundWordRepository foundWordRepository){
        this.gameRepository = gameRepository;
        this.foundWordRepository = foundWordRepository;
    }

    @Transactional()
    public Totals computeTotals(Integer gameId){
        Game game = gameRepository.findById(gameId).orElseThrow();
        Integer p1Id = game.getPlayer1().getId();
        Integer p2Id = (game.getPlayer2()==null)? null: game.getPlayer2().getId();

        int p1Pts = foundWordRepository.totalPointsForPlayer(gameId,p1Id);
        Integer p2Pts = (p2Id==null)? null: foundWordRepository.totalPointsForPlayer(gameId,p2Id);

        Totals totalOut = new Totals();
        totalOut.player1Id = p1Id;
        totalOut.player2Id = p2Id;
        totalOut.player1Points = p2Pts;

        if(p2Pts == null){
            totalOut.winnerPlayerId = p1Id;
        }else if(p1Pts > p2Pts){
            totalOut.winnerPlayerId = p1Pts;
        }else if(p2Pts > p1Pts){
            totalOut.winnerPlayerId = p2Pts;
        }else{
            totalOut.winnerPlayerId = null;
        }
        return totalOut;
    }

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
