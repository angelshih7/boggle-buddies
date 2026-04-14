package com.example.Boggle.Service;

import com.example.Boggle.Model.Tables.FoundWord;
import com.example.Boggle.Service.TrieNodeHelper.WordCandidate;
import com.example.Boggle.repository.FoundWordRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service that compares the words a player found against all valid words
 * on the board, returning them split into found and missed lists.
 */
@Service
public class WordComparisonService {

    public record ComparisonResult(
        List<WordCandidate> foundWords,
        List<WordCandidate> missedWords
    ) {}

    private final GameService gameService;
    private final FindWordsService findWordsService;
    private final FoundWordRepository foundWordRepository;

    public WordComparisonService(GameService gameService,
                                 FindWordsService findWordsService,
                                 FoundWordRepository foundWordRepository) {
        this.gameService = gameService;
        this.findWordsService = findWordsService;
        this.foundWordRepository = foundWordRepository;
    }

    /**
     * Returns all valid words on the board split into two lists:
     * words the player found and words the player missed.
     *
     * @param gameId   the ID of the game
     * @param playerId the ID of the player
     * @return a ComparisonResult containing foundWords and missedWords
     */
    public ComparisonResult compare(Integer gameId, Integer playerId) {
        String boardString = gameService.getBoard(gameId).getBoardString();

        List<WordCandidate> allOnBoard = findWordsService.findValidWords(boardString);

        List<FoundWord> playerFoundWords =
                foundWordRepository.findByGame_IdAndPlayer_IdOrderByFoundAtDesc(gameId, playerId);

        Set<String> foundWordSet = playerFoundWords.stream()
                .map(fw -> fw.getDictionaryWord().getWord().toUpperCase())
                .collect(Collectors.toSet());

        List<WordCandidate> found = allOnBoard.stream()
                .filter(w -> foundWordSet.contains(w.getWord().toUpperCase()))
                .collect(Collectors.toList());

        List<WordCandidate> missed = allOnBoard.stream()
                .filter(w -> !foundWordSet.contains(w.getWord().toUpperCase()))
                .collect(Collectors.toList());

        return new ComparisonResult(found, missed);
    }
}
