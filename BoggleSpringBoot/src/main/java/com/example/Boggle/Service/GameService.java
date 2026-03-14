package com.example.Boggle.Service;

import com.example.Boggle.Model.Controllers.GameController;
import com.example.Boggle.Model.Tables.Board;
import com.example.Boggle.Model.Tables.Game;
import com.example.Boggle.Model.Tables.GameStatus;
import com.example.Boggle.Model.Tables.User;
import com.example.Boggle.repository.BoardRepository;
import com.example.Boggle.repository.GameRepository;
import com.example.Boggle.repository.UserRepository;
import com.example.Boggle.util.ShuffleUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    public GameService(GameRepository gameRepository,
                       BoardRepository boardRepository,
                       UserRepository userRepository) {
        this.gameRepository = gameRepository;
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
    }

    public Game createGame(GameController.GameMode mode, Integer playerId) {
        if (mode == null) {
            throw new ResponseStatusException(BAD_REQUEST, "mode is required");
        }

        User p1 = requireUser(playerId, "playerId");
        Game game;

        switch (mode) {
            case SOLO -> {
                game = new Game(p1, null, createAndSaveBoard());
                game.setStatus(GameStatus.IN_PROGRESS);
                game.setStartedAt(LocalDateTime.now());
            }
            case BOT -> {
                game = new Game(p1, getOrCreateBot(), createAndSaveBoard());
                game.setStatus(GameStatus.IN_PROGRESS);
                game.setStartedAt(LocalDateTime.now());
            }
            case MULTIPLAYER -> {
                game = new Game(p1, null, createAndSaveBoard());
                game.setStatus(GameStatus.WAITING);
                game.setStartedAt(null);
            }
            default -> throw new ResponseStatusException(BAD_REQUEST, "Unknown mode");
        }

        game.setFinishedAt(null);
        return gameRepository.save(game);
    }

    public Game joinGame(Integer gameId, Integer playerId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Game id not found"));

        User player2 = requireUser(playerId, "playerId");

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

        return gameRepository.save(game);
    }

    public Game getGame(Integer gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Game id not found"));
    }

    public Board getBoard(Integer gameId) {
        return getGame(gameId).getBoard();
    }

    private Board createAndSaveBoard() {
        String flattened = ShuffleUtil.shuffledBoard().flattened;

        Board board = new Board();
        board.setBoardId(UUID.randomUUID().toString());
        board.setBoardString(flattened);

        return boardRepository.save(board);
    }

    private User getOrCreateBot() {
        return userRepository.findByUsername("bot")
                .orElseGet(() -> {
                    User bot = new User("bot", "bot@boggle.local", "BOT");
                    bot.setGuest(true);
                    return userRepository.save(bot);
                });
    }

    private User requireUser(Integer userId, String fieldName) {
        if (userId == null) {
            throw new ResponseStatusException(BAD_REQUEST, fieldName + " is required");
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, fieldName + " not found"));
    }
}