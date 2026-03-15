package com.example.Boggle;

import com.example.Boggle.Model.Controllers.GameController;
import com.example.Boggle.Model.Controllers.UserController;
import com.example.Boggle.Model.Tables.Board;
import com.example.Boggle.Model.Tables.Game;
import com.example.Boggle.Model.Tables.GameStatus;
import com.example.Boggle.Model.Tables.User;
import com.example.Boggle.repository.BoardRepository;
import com.example.Boggle.repository.FoundWordRepository;
import com.example.Boggle.repository.GameRepository;
import com.example.Boggle.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.AssertionsKt.assertNotNull;
import static org.junit.jupiter.api.AssertionsKt.assertNull;
import static org.mockito.Mockito.*;

/**
 * Unit test for {@link GameController}
 *
 * These test verify game creation, multiplayer joining,
 * and board retrieval behavior using mocked repositories.
 */
public class UnitAPIGameTest {

    private GameRepository gameRepository;
    private BoardRepository boardRepository;
    private UserRepository userRepository;
    private FoundWordRepository foundWordRepository;
    private GameController gameController;

    /**
     * Initializes mocked repositories and creates a controller instance
     * before each test runs.
     */
    @BeforeEach
    void setup() {
        gameRepository = mock(GameRepository.class);
        boardRepository = mock(BoardRepository.class);
        userRepository = mock(UserRepository.class);
        foundWordRepository = mock(FoundWordRepository.class);

        gameController = new GameController(
                gameRepository,
                boardRepository,
                foundWordRepository,
                userRepository
        );
    }

    /**
     * Verifies that creating a solo player game
     * return the correct game response (board, player) and in-progress status
     */
    @Test
    void TestCreateGameSolo() {
        User currentUser = new User("Diego9", "diego@test.com", "Secret123");
        currentUser.setId(2);

        Board currentBoard = new Board();
        currentBoard.setBoardId("board-123");
        currentBoard.setBoardString("ABCD\nEFGH\nIJKL\nMNOP");

        Game savedGame = new Game(currentUser, null, currentBoard);
        savedGame.setId(10);
        savedGame.setStatus(GameStatus.IN_PROGRESS);

        when(userRepository.findById(2)).thenReturn(Optional.of(currentUser));
        when(boardRepository.save(any(Board.class))).thenReturn(currentBoard);
        when(gameRepository.save(any(Game.class))).thenReturn(savedGame);

        GameController.CreateGameRequest req = new GameController.CreateGameRequest();
        req.mode = GameController.GameMode.SOLO;
        req.playerId = 2;

        GameController.GameResponse response = gameController.createGame(req);

        assertNotNull(response);
        assertEquals(2, response.player1Id);
        assertNull(response.player2Id);
        assertEquals("board-123", response.boardId);
        assertEquals("IN_PROGRESS", response.status);

        verify(userRepository).findById(2);
        verify(boardRepository).save(any(Board.class));
        verify(gameRepository).save(any(Game.class));
        verifyNoInteractions(foundWordRepository);

    }

    /**
     * Verifies that creating a bot game assigns the bot as the 2nd player
     * and returns the correct game response
     */
    @Test
    void TestCreateGameBot(){
        User currentUser = new User("Diego9","diego@test.com","Secret123");
        currentUser.setId(2);

        User userBot = new User("bot","bot@boggle.local","BOT");
        userBot.setId(3);

        Board board = new Board();
        board.setBoardId("board-bot");
        board.setBoardString("ABCD\nEFGH\nIJKL\nMNOP");

        Game savedGame = new Game(currentUser,userBot,board);
        savedGame.setId(11);
        savedGame.setStatus(GameStatus.IN_PROGRESS);

        when(userRepository.findById(2)).thenReturn(Optional.of(currentUser));
        when(userRepository.findByUsername("bot")).thenReturn(Optional.of(userBot));
        when(boardRepository.save(any(Board.class))).thenReturn(board);
        when(gameRepository.save(any(Game.class))).thenReturn(savedGame);

        GameController.CreateGameRequest req = new GameController.CreateGameRequest();
        req.playerId = 2;
        req.mode = GameController.GameMode.BOT;

        GameController.GameResponse response = gameController.createGame(req);

        assertNotNull(response);
        assertEquals(11, response.gameId);
        assertEquals(2, response.player1Id);
        assertEquals(3, response.player2Id);
        assertEquals("board-bot", response.boardId);
        assertEquals("IN_PROGRESS", response.status);

    }

    /**
     * Verifies that creating a multiplayer game places the game
     * in waiting status until another player joins.
     */
    @Test
    void TestCreateGameMultiplayer(){
        User firstPlayer = new User("Diego9","diego@test.com","Secret123");
        firstPlayer.setId(2);

        Board board = new Board();
        board.setBoardId("board-Multiplayer");
        board.setBoardString("ABCD\nEFGH\nIJKL\nMNOP");

        Game savedGame = new Game(firstPlayer,null,board);
        savedGame.setId(11);
        savedGame.setStatus(GameStatus.WAITING);

        when(userRepository.findById(2)).thenReturn(Optional.of(firstPlayer));
        when(gameRepository.save(any(Game.class))).thenReturn(savedGame);
        when(boardRepository.save(any(Board.class))).thenReturn(board);

        GameController.CreateGameRequest req = new GameController.CreateGameRequest();
        req.mode = GameController.GameMode.MULTIPLAYER;
        req.playerId = 2;

        GameController.GameResponse response = gameController.createGame(req);

        assertNotNull(response);
        assertEquals(11, response.gameId);
        assertEquals("board-Multiplayer", response.boardId);
        assertEquals(2, response.player1Id);
        assertNull(response.player2Id);
        assertEquals("board-Multiplayer", response.boardId);
        assertEquals("WAITING", response.status);
    }

    /**
     * Verifies that a second player can successfully join an
     * existing waiting multiplayer game.
     */
    @Test
    void testJoinGameSuccess(){
        User firstPlayer = new User("Diego9","diego@test.com","Secret123");
        firstPlayer.setId(1);

        User secondPlayer = new User("james9","james@test.com","Secret456");
        secondPlayer.setId(2);

        Board board = new Board();
        board.setBoardId("Board-join");
        board.setBoardString("ABCD\nEFGH\nIJKL\nMNOP");

        Game existingGame = new Game(firstPlayer,null,board);
        existingGame.setId(20);
        existingGame.setStatus(GameStatus.WAITING);

        Game savedGame = new Game(firstPlayer,secondPlayer,board);
        savedGame.setId(20);
        savedGame.setStatus(GameStatus.IN_PROGRESS);

        when(gameRepository.findById(20)).thenReturn(Optional.of(existingGame));
        when(userRepository.findById(2)).thenReturn(Optional.of(secondPlayer));
        when(gameRepository.save(any(Game.class))).thenReturn(savedGame);

        GameController.JoinGameRequest req = new GameController.JoinGameRequest();
        req.playerId = 2;

        GameController.GameResponse response = gameController.joinGame(20, req);

        assertNotNull(response);
        assertEquals(20, response.gameId);
        assertEquals(1, response.player1Id);
        assertEquals(2, response.player2Id);
        assertEquals("Board-join", response.boardId);
        assertEquals("IN_PROGRESS", response.status);

    }

    /**
     * Verifies that joining a game with a null request body
     * throws a bad request exception.
     */
    @Test
    void testJoinGameNullBody() {
        ResponseStatusException ex =
                assertThrows(ResponseStatusException.class, () -> gameController.joinGame(20, null));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    /**
     * Verifies that attempting to join a non-existent game
     * throws a not found exception.
     */
    @Test
    void testJoinGameNotFound(){
        when(gameRepository.findById(50)).thenReturn(Optional.empty());

        GameController.JoinGameRequest req = new GameController.JoinGameRequest();
        req.playerId = 2;

        ResponseStatusException ex =
                assertThrows(ResponseStatusException.class, () -> gameController.joinGame(50, req));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    /**
     * Verifies that retrieving a board for an existing game
     * returns the expected board identifier and board contents.
     */
    @Test
    void testGetBoard_success() {
        User player1 = new User("Diego9", "diego@test.com", "Secret123");
        player1.setId(2);

        Board board = new Board();
        board.setBoardId("board-2");
        board.setBoardString("ABCD\nEFGH\nIJKL\nMNOP");

        Game game = new Game(player1, null, board);
        game.setId(50);

        when(gameRepository.findById(50)).thenReturn(Optional.of(game));

        GameController.BoardResponse response = gameController.getBoard(50);

        assertNotNull(response);
        assertEquals("board-2", response.boardId);
        assertEquals("ABCD\nEFGH\nIJKL\nMNOP", response.boardString);    }
}
