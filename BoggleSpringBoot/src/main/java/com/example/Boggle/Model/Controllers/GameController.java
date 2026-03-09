package com.example.Boggle.Model.Controllers;
import com.example.Boggle.Model.Tables.Board;
import com.example.Boggle.Model.Tables.Game;
import com.example.Boggle.Model.Tables.GameStatus;
import com.example.Boggle.Model.Tables.User;
import com.example.Boggle.repository.BoardRepository;
import com.example.Boggle.repository.FoundWordRepository;
import com.example.Boggle.repository.GameRepository;
import com.example.Boggle.repository.UserRepository;
import com.example.Boggle.util.ShuffleUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;
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
    private final UserRepository userRepository;

    //initialize repositories
    public GameController(GameRepository gameRepository,
                          BoardRepository boardRepository,
                          FoundWordRepository foundWordRepository,
                          UserRepository userRepository){
        this.gameRepository = gameRepository;
        this.boardRepository = boardRepository;
        this.foundWordRepository = foundWordRepository;
        this.userRepository = userRepository;
    }

    public enum GameMode {SOLO, BOT, MULTIPLAYER}

    public static class CreateGameRequest{
        public GameMode mode;
        //solo /Bot
        public Integer playerId;
        //multiplayer
        public Integer gameId;
    }

    // format for a json response to send to frontend
    public static class GameResponse{
        public Integer gameId;
        public Integer player1Id;
        public Integer player2Id;
        public String boardId;
        public String status;
        public static GameResponse GameSummaryDTO(Game currentGame){
            GameResponse gameSummary = new GameResponse();
            gameSummary.gameId = currentGame.getId();
            gameSummary.player1Id = currentGame.getPlayer1().getId();
            gameSummary.player2Id = (currentGame.getPlayer2() == null) ? null : currentGame.getPlayer2().getId();
            gameSummary.boardId = currentGame.getBoard().getBoardId();
            gameSummary.status = currentGame.getStatus().name();
            return gameSummary;
        }
    }

    public static class BoardResponse{
        public String boardId;
        public String boardString;
        public BoardResponse(String boardId, String boardString ){
            this.boardId = boardId;
            this.boardString = boardString;
        }
        public static BoardResponse BoardDTO(Board currentBoard){
            return new BoardResponse(currentBoard.getBoardId(), currentBoard.getBoardString());
        }
    }

    /* Needs refactoring. Session class removed.
    // New endpoint: submit a word, reject duplicates per (gameCode, username)
    @PostMapping("/game/submitWord")
    public SubmitWordResponse submitWord(@RequestBody SubmitWordRequest request) {
        if (request.getSessionCode() == null || request.getUsername() == null || request.getWord() == null) {
            return new SubmitWordResponse(false, "INVALID");
        }

        try {
            Session session = findSession(request.getSessionCode());

            // Optional: auto-add user if they somehow submit before joining
            session.addUser(request.getUsername());

            boolean accepted = session.recordWord(request.getUsername(), request.getWord());
            if (!accepted) {
                return new SubmitWordResponse(false, "DUPLICATE");
            }
            return new SubmitWordResponse(true, "OK");

        } catch (NoSuchElementException e) {
            return new SubmitWordResponse(false, "INVALID");
        }
    }
    */

    //=====Solo /Bot game =======/
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/game")
    public GameResponse createGame(@RequestBody CreateGameRequest request){
        if(request == null || request.mode == null){
            throw new ResponseStatusException(BAD_REQUEST,"Body is required");
        }

        User p1;
        User p2;
        Game game;

        switch (request.mode) {
            case SOLO ->{
                p1 = requireUser(request.playerId,"playerId");
                p2 = null;
                Board board = createAndSaveBoard();
                game = new Game(p1,p2,board);
            }
            case BOT -> {
                p1 = requireUser(request.playerId,"PlayerId");
                p2 = getOrCreateBot();
                Board board = createAndSaveBoard();
                game = new Game(p1,p2,board);
            }
            case MULTIPLAYER ->{
                // Attempted to adapt previously-Session-related join functionality here.
                // Need game objects to test.
                try {
                    // Game was created by p1 and already exists, join
                    game = gameRepository.getReferenceById(request.gameId);
                    game.setPlayer2(requireUser(request.playerId,"PlayerId"));
                } catch (EntityNotFoundException e) {
                    // Game not created yet, become p1
                    Board board = createAndSaveBoard();
                    p1 = requireUser(request.playerId,"PlayerId");
                    game = new Game(p1,null,board);
                }
            }
            default -> throw new ResponseStatusException(BAD_REQUEST, "Unknown mode");
        }

        game.setStatus(GameStatus.IN_PROGRESS);
        game.setStartedAt(LocalDateTime.now());

    return GameResponse.GameSummaryDTO(gameRepository.save(game));
    }

    @GetMapping("/game/{gameId}")
    public GameResponse getGame(@PathVariable Integer gameId){
        Game gameSelected = gameRepository.findById(gameId)
                .orElseThrow(()-> new ResponseStatusException(NOT_FOUND,"Game id not found."));
        return GameResponse.GameSummaryDTO(gameSelected);
    }

    @GetMapping("/game/{gameId}/board")
    public BoardResponse getBoard(@PathVariable Integer gameId){
        Game gameBoardSelect = gameRepository.findById(gameId)
                .orElseThrow(()-> new ResponseStatusException(NOT_FOUND,"board related to game not found"));
        return BoardResponse.BoardDTO(gameBoardSelect.getBoard());
    }

    @PostMapping("/game/board")
    public BoardResponse getBoardSample(){
        Board boardSample = createAndSaveBoard();
        return BoardResponse.BoardDTO(boardSample);    }

    //=====Helper Methods======/
    private Board createAndSaveBoard(){
        String flattened  = ShuffleUtil.shuffle_board().flattened;
        Board newBoard = new Board();
        newBoard.setBoardId(UUID.randomUUID().toString());
        newBoard.setBoardString(flattened);
        return boardRepository.save(newBoard);
    }

    private String require(String value, String field){
        if(value==null || value.isBlank()){
            throw new ResponseStatusException(BAD_REQUEST,field + " is required");
        }
        return value.trim();
    }
    private User getOrCreateBot(){
        return userRepository.findByUsername("bot").orElseGet(
                ()-> userRepository.save(new User("bot","bot@boggle.local","BOT")));
    }
    private User requireUser(Integer userId, String fieldName){
        if(userId==null){
            throw new ResponseStatusException(BAD_REQUEST,fieldName + " is required");
        }
        return userRepository.findById(userId).orElseThrow(
                ()-> new ResponseStatusException(NOT_FOUND, fieldName + " not found")
        );
    }
}