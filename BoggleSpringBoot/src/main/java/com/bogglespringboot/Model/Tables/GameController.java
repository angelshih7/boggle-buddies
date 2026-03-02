package com.bogglespringboot.Game;

import com.bogglespringboot.Model.Tables.*;
import com.bogglespringboot.Session.Session;
import com.bogglespringboot.repository.BoardRepository;
import com.bogglespringboot.repository.GameRepository;
import com.bogglespringboot.repository.SessionRepository;
import com.bogglespringboot.repository.UserRepository;
import com.bogglespringboot.util.ShuffleUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.*;


/*
Rest API for games table manager.
It manages every request to the backend by frontend in relation to the table.
 */

@RestController
@RequestMapping("/api")
public class GameController{

    //hold value of the repository created
    private final GameRepository gameRepository;
    private final BoardRepository boardRepository;
    private final FoundWordRepository foundWordRepository;
    private final SessionRepository sessionRepository;


//initialize repositories
    public GameManager(GameRepository gameRepository,
                       BoardRepository boardRepository,
                       FoundWordRepository foundWordRepository,
                       SesssionRepository sesssionRepository){
        this.gameRepository = gameRepository;
        this.boardRepository = boardRepository;
        this.foundWordRepository = foundWordRepository;
        this.sessionRepository = sesssionRepository;
    }

    public enum GameMode {SOLO, BOT, MULTIPLAYER}

    public static class CreateGameRequest{
        public GameMode mode;
        //solo /Bot
        public Integer playerId;
        //multiplayer
        public string sessionCode;
    }

    // format for a json response to send to frontend
    public static class GameResponse{
        public Integer gameId;
        public Integer player1Id;
        public Integer player2Id;
        public Integer boardId;
        public String boardId;
        public String status;
        public static GameResponse GameSummaryDTO(Game currentGame){
            GameResponse gameSummary = new GameResponse();
            gameSummary.gameId = currentGame.getId();
            gameSummary.player1Id = currentGame.getPlayer1.getId();
            gameSummary.player2Id = currentGame.getPlayer2.getId();
            gameSummary.boardId = currentGame.getBoard().getBoardId();
            gameSummary.status = currentGame.getStatus.name();
            return gameSummary;
        }
    }

    public static class BoardResponse{
        public String boardId;
        public String boardString;
        public BoardResponse BoardGetInstance (String boardId, String boardString ){
            this.boardId = boardId;
            this.boardString = boardString
        }
        public static BoardResponse BoadDTO(Board currentBoard){
            return new BoardResponse(currentBoard.getBoardId(),currentBoard.getBoardString);
        }
    }



    //=====Solo /Bot game =======/
    @GetMapping("/game");
    public GameResponse creatGame(@RequestBody CreateGameRequest request){
        if(request == null || request.mode == null){
            throw new ResponseStatusException(BAD_REQUEST,"Body is required");
        }

        User

        switch (GameMode):
            case SOLO:

            case BOT:
            case Multiplayer:


    }

    //method to return board generated for game and board_id.


    //method to see

    @PostMapping("/game")






}