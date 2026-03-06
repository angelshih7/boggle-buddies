package com.bogglespringboot.Service;


import com.bogglespringboot.repository.FoundWordRepository;
import com.bogglespringboot.repository.GameRepository;
import org.springframework.stereotype.Service;

@Service
public class GameScoreService {
    public static class Total{
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




}
