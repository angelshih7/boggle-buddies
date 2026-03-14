package com.example.Boggle.Model.Controllers;
import com.example.Boggle.Model.Tables.Board;
import com.example.Boggle.Model.Tables.Game;
import com.example.Boggle.Model.Tables.GameStatus;
import com.example.Boggle.Model.Tables.User;
import com.example.Boggle.Service.GameScoreService;
import com.example.Boggle.Service.GameService;
import com.example.Boggle.Service.WordSubmissionService;
import com.example.Boggle.Session.SubmitWordRequest;
import com.example.Boggle.repository.BoardRepository;
import com.example.Boggle.repository.FoundWordRepository;
import com.example.Boggle.repository.GameRepository;
import com.example.Boggle.repository.UserRepository;
import com.example.Boggle.util.ShuffleUtil;
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


    private final GameService gameService;
    private final GameScoreService gameScoreService;
    private final WordSubmissionService wordSubmissionService;

    /**
     * Creates a controller with the repositories required to manage games
     * and boards.
     *
     * Related classes to check out {@link GameService} {@link GameScoreService} {@link WordSubmissionService}
     *
     * @param gameService Service that handles game logic
     * @param gameScoreService Service that handles score tracking
     * @param wordSubmissionService Service that handles word submission validation
     */
    public GameController(GameService gameService,
                          GameScoreService gameScoreService,
                          WordSubmissionService wordSubmissionService){
       this.gameService = gameService;
       this.gameScoreService = gameScoreService;
       this. wordSubmissionService = wordSubmissionService;
    }

    public enum GameMode {SOLO, BOT, MULTIPLAYER}

    public static class CreateGameRequest{
        public GameMode mode;
        public Integer playerId;
    }

    public static class JoinGameRequest{
        public Integer playerId;
    }

    /**
     *
     */
    public static class SubmitWordRequest {
        public Integer playerId;
        public String word;
    }

    /**
     * Response body summarizing a game.
     */
    public static class GameResponse{
        public Integer gameId;
        public Integer player1Id;
        public Integer player2Id;
        public String boardId;
        public String status;
        public LocalDateTime createdAt;
        public LocalDateTime startedAt;
        public LocalDateTime finishedAt;

        /**
         * Builds a response DTO from a game entity.
         *
         * @param currentGame the game entity to summarize
         * @return a response containing key game details
         */
        public static GameResponse GameSummaryDTO(Game currentGame){
            GameResponse gameSummary = new GameResponse();
            gameSummary.gameId = currentGame.getId();
            gameSummary.player1Id = currentGame.getPlayer1().getId();
            gameSummary.player2Id = (currentGame.getPlayer2() == null) ? null : currentGame.getPlayer2().getId();
            gameSummary.boardId = currentGame.getBoard().getBoardId();
            gameSummary.status = currentGame.getStatus().name();
            gameSummary.createdAt = currentGame.getCreatedAt();
            gameSummary.startedAt = currentGame.getStartedAt();
            gameSummary.finishedAt = currentGame.getFinishedAt();
            return gameSummary;
        }
    }

    public static class BoardResponse{

        public String boardId;
        public String boardString;

        /**
         * Builds a response DTO from a board entity.
         *
         * @param currentBoard the board entity
         * @return a response containing board details
         */
        public static BoardResponse BoardDTO(Board currentBoard){
            BoardResponse boardOut = new BoardResponse();
            boardOut.boardId = currentBoard.getBoardId();
            boardOut.boardString = currentBoard.getBoardString();
            return boardOut;
        }
    }

    public static class SubmitWordResponse{
        public boolean accepted;
        public String reason;
        public String normalizedWord;
        public Integer points;

        public static SubmitWordResponse SubmitWordDTO(WordSubmissionService.Result result){
            SubmitWordResponse submitWordResult = new SubmitWordResponse();
            submitWordResult.accepted = result.accepted;
            submitWordResult.reason = result.reason.name();
            submitWordResult.normalizedWord = result.normalizedWord;
            submitWordResult.points = result.points;
            return submitWordResult;
        }
    }

    //=====Solo /Bot game =======/
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/game")
    public GameResponse createGame(@RequestBody CreateGameRequest request){
        if(request == null){
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Body is required");
        }
        if(request.playerId == null){
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "playerId is required");
        }
        if(request.mode == null){
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Mode is required");
        }

        Game game = gameService.createGame(request.mode,request.playerId);
        return GameResponse.GameSummaryDTO(game);
    }


    @PostMapping("/game/{gameId}/join")
    public GameResponse joinGame(@PathVariable Integer gameId, @RequestBody JoinGameRequest request){
        if (request == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Body is required");
        }
        if (request.playerId == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "playerId is required");
        }

        Game game = gameService.joinGame(gameId, request.playerId);
        return GameResponse.GameSummaryDTO(game);
    }

    @GetMapping("/game/{gameId}")
    public GameResponse getGame(@PathVariable Integer gameId){
        return GameResponse.GameSummaryDTO(gameService.getGame(gameId));
    }

    @GetMapping("/game/{gameId}/board")
    public BoardResponse getBoard(@PathVariable Integer gameId){
        return BoardResponse.BoardDTO(gameService.getBoard(gameId));
    }

    /**
     * Retrieves a summary of the Submit
     *
     * @param gameId
     * @param request
     * @return
     */
    @PostMapping("/game/{gameId}/submit-word")
    public SubmitWordResponse submitWord(@PathVariable Integer gameId,
                                         @RequestBody SubmitWordRequest request){
        if(request == null){
            throw new org.springframework.web.server.ResponseStatusException(
                    BAD_REQUEST,"playerId is required");
        }
        if (request.playerId == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "playerId is required");
        }
        if (request.word == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "word is required");
        }
        WordSubmissionService.Result result =
                wordSubmissionService.submitWord(gameId, request.playerId, request.word);

        return SubmitWordResponse.SubmitWordDTO(result);
    }

    /**
     *
     * @param gameId
     * @return
     */
    @GetMapping("/game/{gameId}/score")
    public GameScoreService.Totals getScore(@PathVariable Integer gameId){
        return gameScoreService.computeTotals(gameId);
    }

    /**
     *
     * @param gameId
     * @return
     */
    @PostMapping("/game/{gameId}/finish")
    public GameScoreService.Totals finishGame(@PathVariable Integer gameId){
        return gameScoreService.finishGame(gameId);
    }
}
