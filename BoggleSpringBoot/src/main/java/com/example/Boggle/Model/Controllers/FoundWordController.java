import com.example.Boggle.Model.Tables.FoundWord;
package com.example.Boggle.Model.Controllers;
import static org.springframework.http.HttpStatus.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/game")
public class FoundWordController {

    @Autowired
    private FoundWordRepository foundWordRepository;

    /**
     * GET /api/game/{gameId}/player/{playerId}/words
     * Returns the list of unique words found by a specific player in a specific game.
     */
    @GetMapping("/{gameId}/player/{playerId}/words")
    public ResponseEntity<List<FoundWordResponse>> getPlayerFoundWords(
            @PathVariable Integer gameId,
            @PathVariable Integer playerId) {

        List<FoundWord> foundWords = foundWordRepository.findByGame_IdAndPlayer_Id(gameId, playerId);

        // Map the entities to our lightweight DTO
        List<FoundWordResponse> response = foundWords.stream()
                .map(fw -> new FoundWordResponse(
                        fw.getDictionaryWord().getWord(),      // The actual string "APPLE"
                        fw.getDictionaryWord().getPointValue(), // Points for the word
                        fw.getFoundAt()                         // When it was found
                ))
                .toList();

        return ResponseEntity.ok(response);
    }
}