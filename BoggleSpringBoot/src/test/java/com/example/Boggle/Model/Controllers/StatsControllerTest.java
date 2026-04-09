package com.example.Boggle.Model.Controllers;

import com.example.Boggle.Model.Tables.*;
import com.example.Boggle.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StatsControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private DictionaryRepository dictionaryRepository;

    @Autowired
    private FoundWordRepository foundWordRepository;

    @Test
    @Transactional // Rolls back the data after the test so your DB stays clean
    void testGetUserStatsApi() throws Exception {
        // 1. Create a User
        User user = new User();
        user.setUsername("stats_user");
        user.setEmail("stats@test.com");
        user.setPasswordHash("dummy_hash");
        user = userRepository.save(user);
        Integer userId = user.getId();

        // 2. Create a Board
        Board board = new Board();
        board.setBoardId("test_board_123");
        board.setBoardString("ABCDEFGHIJKLMNOP");
        boardRepository.save(board);

        // 3. Create a Game that the user PLAYED and WON
        Game game1 = new Game();
        game1.setPlayer1(user);
        game1.setBoard(board);
        game1.setStatus("FINISHED");
        game1.setWinnerPlayerId(userId); // User won
        gameRepository.save(game1);

        // 4. Create a Game that the user PLAYED but LOST
        Game game2 = new Game();
        game2.setPlayer1(user);
        game2.setBoard(board);
        game2.setStatus("FINISHED");
        game2.setWinnerPlayerId(999); // Someone else won
        gameRepository.save(game2);

        // 5. Add a "Found Word" for this user
        Dictionary word = new Dictionary();
        word.setWord("test");
        word.setPointValue(1);
        dictionaryRepository.save(word);

        FoundWord fw = new FoundWord();
        fw.setPlayer(user);
        fw.setGame(game1);
        fw.setDictionaryWord(word);
        foundWordRepository.save(fw);

        // 6. Execute API Request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/users/" + userId + "/stats"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // 7. Verify Results
        assertEquals(200, response.statusCode());

        // Use Jackson to parse the JSON response
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(response.body());

        // Note: Spring Projections convert "getGamesPlayed()" to "gamesPlayed" in JSON
        assertEquals(2, json.get("gamesPlayed").asInt(), "Expected 2 games played");
        assertEquals(1, json.get("gamesWon").asInt(), "Expected 1 game won");
        assertEquals(1, json.get("wordsFound").asInt(), "Expected 1 word found");

        client.close();
    }
}