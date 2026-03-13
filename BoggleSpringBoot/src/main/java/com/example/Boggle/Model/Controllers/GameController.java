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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;
/**
 * REST controller for creating, joining, and retrieving Boggle games.
 *
 * <p>This controller supports solo, bot, and multiplayer game creation,
 * allows a second player to join a waiting multiplayer game, and exposes
 * endpoints for retrieving game and board information.
 */
@RestController
@RequestMapping("/api")
public class GameController{

    private final GameRepository gameRepository;
    private final BoardRepository boardRepository;
    private final FoundWordRepository foundWordRepository;
    private final UserRepository userRepository;

    /**
     * Creates a controller with the repositories required to manage games
     * and boards.
     *
     * @param gameRepository repository for game records
     * @param boardRepository repository for board records
     * @param foundWordRepository repository for submitted words
     * @param userRepository repository for users
     */
    public GameController(GameRepository gameRepository,
                          BoardRepository boardRepository,
                          FoundWordRepository foundWordRepository,
                          UserRepository userRepository){
        this.gameRepository = gameRepository;
        this.boardRepository = boardRepository;
        this.foundWordRepository = foundWordRepository;
        this.userRepository = userRepository;
    }

    /**
     * Supported game modes for game creation.
     */
    public enum GameMode {SOLO, BOT, MULTIPLAYER}

    /**
     * Request body for creating a new game.
     */
    public static class CreateGameRequest{
        /**
         * The requested game mode.
         */
        public GameMode mode;

        /**
         * The ID of the player creating the game.
         */
        public Integer playerId;
    }
    /**
     * Request body for joining an existing multiplayer game.
     */
    public static class JoinGameRequest{
        /**
         * The ID of the player joining the game.
         */
        public Integer playerId;
    }

    /**
     * Response body summarizing a game.
     */
    public static class GameResponse{
        /**
         * The game ID.
         */
        public Integer gameId;

        /**
         * The ID of player one.
         */
        public Integer player1Id;

        /**
         * The ID of player two, or {@code null} if no second player has joined.
         */
        public Integer player2Id;

        /**
         * The public board identifier associated with the game.
         */
        public String boardId;

        /**
         * The current game status.
         */
        public String status;

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
            return gameSummary;
        }
    }

    /**
     * Response body containing board information.
     */
    public static class BoardResponse{
        /**
         * The public board identifier.
         */
        public String boardId;

        /**
         * The flattened board string representation.
         */
        public String boardString;

        /**
         * Creates a board response.
         *
         * @param boardId the public board identifier
         * @param boardString the flattened board contents
         */
        public BoardResponse(String boardId, String boardString ){
            this.boardId = boardId;
            this.boardString = boardString;
        }

        /**
         * Builds a response DTO from a board entity.
         *
         * @param currentBoard the board entity
         * @return a response containing board details
         */
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

    /**
     * Creates a new game in solo, bot, or multiplayer mode.
     *
     * <p>Solo and bot games begin immediately. Multiplayer games are created
     * in a waiting state until a second player joins.
     *
     * @param request the game creation request
     * @return a summary of the created game
     * @throws ResponseStatusException if the request is invalid or the player does not exist
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/game")
    public GameResponse createGame(@RequestBody CreateGameRequest request){
        if(request == null || request.mode == null){
            throw new ResponseStatusException(BAD_REQUEST,"Body is required");
        }

        User p1 = requireUser(request.playerId, "playerId");
        Game game;

        switch (request.mode) {
            case SOLO ->{
                game = new Game(p1,null,createAndSaveBoard());

            }
            case BOT -> {
                game = new Game(p1,getOrCreateBot(),createAndSaveBoard());
            }
            case MULTIPLAYER -> game = new Game(p1, null, createAndSaveBoard());

            default -> throw new ResponseStatusException(BAD_REQUEST, "Unknown mode");
        }

        if(request.mode ==GameMode.MULTIPLAYER){
            game.setStatus(GameStatus.WAITING);
        }else{
            game.setStatus(GameStatus.IN_PROGRESS);
            game.setStartedAt(LocalDateTime.now());
        }

    return GameResponse.GameSummaryDTO(gameRepository.save(game));
    }

    /**
     * Joins an existing multiplayer game as the second player.
     *
     * @param gameId the ID of the game to join
     * @param request the join request containing the joining player's ID
     * @return a summary of the updated game
     * @throws ResponseStatusException if the game does not exist, is not
     *         joinable, is already full, or the player is invalid
     */
    @PostMapping("/game/{gameId}/join")
    public GameResponse joinGame(@PathVariable Integer gameId, @RequestBody JoinGameRequest request){
        if (request == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Body is required");
        }

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Game id not found"));

        User player2 = requireUser(request.playerId, "playerId");

        if (game.getStatus() != GameStatus.WAITING) {
            throw new ResponseStatusException(CONFLICT, "Game is not joinable");
        }

        if (game.getPlayer2() != null) {
            throw new ResponseStatusException(CONFLICT, "Game already full");
        }

        if (game.getPlayer1().getId().equals(player2.getId())) {
            throw new ResponseStatusException(BAD_REQUEST, "Player cannot join own game");
        }

        game.setPlayer2(player2);
        game.setStatus(GameStatus.IN_PROGRESS);
        game.setStartedAt(LocalDateTime.now());

        return GameResponse.GameSummaryDTO(gameRepository.save(game));
    }

    /**
     * Retrieves a summary of a game by ID.
     *
     * @param gameId the game ID
     * @return a summary of the requested game
     * @throws ResponseStatusException if the game does not exist
     */
    @GetMapping("/game/{gameId}")
    public GameResponse getGame(@PathVariable Integer gameId){
        Game gameSelected = gameRepository.findById(gameId)
                .orElseThrow(()-> new ResponseStatusException(NOT_FOUND,"Game id not found."));
        return GameResponse.GameSummaryDTO(gameSelected);
    }

    /**
     * Retrieves a summary of the board related to the gameID passed.
     *
     * @param gameId
     * @return
     */
    @GetMapping("/game/{gameId}/board")
    public BoardResponse getBoard(@PathVariable Integer gameId){
        Game gameBoardSelect = gameRepository.findById(gameId)
                .orElseThrow(()-> new ResponseStatusException(NOT_FOUND,"Board related to game not found"));
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
