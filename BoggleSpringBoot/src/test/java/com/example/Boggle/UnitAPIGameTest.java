package com.example.Boggle;

import com.example.Boggle.Model.Controllers.GameController;
import com.example.Boggle.Model.Controllers.UserController;
import com.example.Boggle.Model.Tables.Board;
import com.example.Boggle.Model.Tables.Game;
import com.example.Boggle.Model.Tables.GameStatus;
import com.example.Boggle.Model.Tables.User;
import com.example.Boggle.Service.GameScoreService;
import com.example.Boggle.Service.GameService;
import com.example.Boggle.Service.WordSubmissionService;
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

public class UnitAPIGameTest {

    private GameController gameController;
    private GameService gameService;
    private GameScoreService gameScoreService;
    private WordSubmissionService wordSubmissionService;


    @BeforeEach
    void setup() {
        gameService = mock(GameService.class);
        gameScoreService = mock(GameScoreService.class);
        wordSubmissionService = mock(WordSubmissionService.class);

        gameController = new GameController(
                gameService,
                gameScoreService,
                wordSubmissionService
        );
    }


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

        GameController.CreateGameRequest req = new GameController.CreateGameRequest();
        req.mode = GameController.GameMode.SOLO;
        req.playerId = 2;

        when(gameService.createGame(GameController.GameMode.SOLO, 2)).thenReturn(savedGame);

        GameController.GameResponse response = gameController.createGame(req);

        assertNotNull(response);
        assertEquals(10, response.gameId);
        assertEquals(2, response.player1Id);
        assertNull(response.player2Id);
        assertEquals("board-123", response.boardId);
        assertEquals("IN_PROGRESS", response.status);

        verify(gameService).createGame(GameController.GameMode.SOLO, 2);
        verifyNoInteractions(gameScoreService, wordSubmissionService);
    }

    @Test
    void TestCreateGameBot() {
        User currentUser = new User("Diego9", "diego@test.com", "Secret123");
        currentUser.setId(2);

        User userBot = new User("bot", "bot@boggle.local", "BOT");
        userBot.setId(3);

        Board board = new Board();
        board.setBoardId("board-bot");
        board.setBoardString("ABCD\nEFGH\nIJKL\nMNOP");

        Game savedGame = new Game(currentUser, userBot, board);
        savedGame.setId(11);
        savedGame.setStatus(GameStatus.IN_PROGRESS);

        GameController.CreateGameRequest req = new GameController.CreateGameRequest();
        req.playerId = 2;
        req.mode = GameController.GameMode.BOT;

        when(gameService.createGame(GameController.GameMode.BOT, 2)).thenReturn(savedGame);

        GameController.GameResponse response = gameController.createGame(req);

        assertNotNull(response);
        assertEquals(11, response.gameId);
        assertEquals(2, response.player1Id);
        assertEquals(3, response.player2Id);
        assertEquals("board-bot", response.boardId);
        assertEquals("IN_PROGRESS", response.status);

        verify(gameService).createGame(GameController.GameMode.BOT, 2);
    }

    @Test
    void TestCreateGameMultiplayer() {
        User firstPlayer = new User("Diego9", "diego@test.com", "Secret123");
        firstPlayer.setId(2);

        Board board = new Board();
        board.setBoardId("board-Multiplayer");
        board.setBoardString("ABCD\nEFGH\nIJKL\nMNOP");

        Game savedGame = new Game(firstPlayer, null, board);
        savedGame.setId(11);
        savedGame.setStatus(GameStatus.WAITING);

        GameController.CreateGameRequest req = new GameController.CreateGameRequest();
        req.mode = GameController.GameMode.MULTIPLAYER;
        req.playerId = 2;

        when(gameService.createGame(GameController.GameMode.MULTIPLAYER, 2)).thenReturn(savedGame);

        GameController.GameResponse response = gameController.createGame(req);

        assertNotNull(response);
        assertEquals(11, response.gameId);
        assertEquals("board-Multiplayer", response.boardId);
        assertEquals(2, response.player1Id);
        assertNull(response.player2Id);
        assertEquals("WAITING", response.status);

        verify(gameService).createGame(GameController.GameMode.MULTIPLAYER, 2);
    }

    @Test
    void testJoinGameSuccess() {
        User firstPlayer = new User("Diego9", "diego@test.com", "Secret123");
        firstPlayer.setId(1);

        User secondPlayer = new User("james9", "james@test.com", "Secret456");
        secondPlayer.setId(2);

        Board board = new Board();
        board.setBoardId("Board-join");
        board.setBoardString("ABCD\nEFGH\nIJKL\nMNOP");

        Game savedGame = new Game(firstPlayer, secondPlayer, board);
        savedGame.setId(20);
        savedGame.setStatus(GameStatus.IN_PROGRESS);

        GameController.JoinGameRequest req = new GameController.JoinGameRequest();
        req.playerId = 2;

        when(gameService.joinGame(20, 2)).thenReturn(savedGame);

        GameController.GameResponse response = gameController.joinGame(20, req);

        assertNotNull(response);
        assertEquals(20, response.gameId);
        assertEquals(1, response.player1Id);
        assertEquals(2, response.player2Id);
        assertEquals("Board-join", response.boardId);
        assertEquals("IN_PROGRESS", response.status);

        verify(gameService).joinGame(20, 2);
    }

    @Test
    void testJoinGameNullBody() {
        ResponseStatusException ex =
                assertThrows(ResponseStatusException.class, () -> gameController.joinGame(20, null));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void testJoinGameNotFound() {
        GameController.JoinGameRequest req = new GameController.JoinGameRequest();
        req.playerId = 2;

        when(gameService.joinGame(50, 2))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Game id not found"));

        ResponseStatusException ex =
                assertThrows(ResponseStatusException.class, () -> gameController.joinGame(50, req));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testGetBoard_success() {
        Board board = new Board();
        board.setBoardId("board-2");
        board.setBoardString("ABCD\nEFGH\nIJKL\nMNOP");

        when(gameService.getBoard(50)).thenReturn(board);

        GameController.BoardResponse response = gameController.getBoard(50);

        assertNotNull(response);
        assertEquals("board-2", response.boardId);
        assertEquals("ABCD\nEFGH\nIJKL\nMNOP", response.boardString);

        verify(gameService).getBoard(50);
    }
}