package com.example.Boggle.Service;

import com.example.Boggle.Model.Tables.Dictionary;
import com.example.Boggle.Model.Tables.FoundWord;
import com.example.Boggle.Model.Tables.Game;
import com.example.Boggle.Model.Tables.User;
import com.example.Boggle.repository.DictionaryRepository;
import com.example.Boggle.repository.FoundWordRepository;
import com.example.Boggle.repository.GameRepository;
import com.example.Boggle.repository.UserRepository;
import com.example.Boggle.Model.Tables.*;
import com.example.Boggle.util.ValidateAnswers;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import com.example.Boggle.util.unflatten;

/**
 *Service responsible for validating word submission during game.
 *
 * Provides the Following Services:
 * 1. Checks if the Game and Player Exist (avoids allowing word submission with no game or player attached)
 * 2. Checks if Player belongs to the game to which the submission is taking place.
 * 3. Checks if the word submitted exist in the dictionary.
 * 4. Checks if the word is long enough
 * 5. Checks if the word is valid in the board (side-by-side or diagonal)
 * 6. Checks if the word has not been submitted (avoid duplicates)
 */
public class WordSubmissionService{

    //Data Structure for result of word submission.
    @Service
    /**
     * Data Structure
     * Holds the result of a word submission attempt.
     */
    public static class Result{
        public boolean accepted;
        public String reason;
        public String normalizedWord;
        public Integer points;
    }

    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final DictionaryRepository dictionaryRepository;
    private final FoundWordRepository foundWordRepository;

    /**
     * Constructs a WordSubmissionService with the repositories required
     * to validate submitted words and store accepted results.
     *
     * @param gameRepository Repository that stores game information.
     * @param userRepository Repository storing the user and guest.
     * @param dictionaryRepository Repository that stores words and their point scoring
     * @param foundWordRepository Repository that holds words found by player during game.
     */
    public WordSubmissionService(
            GameRepository gameRepository,
            UserRepository userRepository,
            DictionaryRepository dictionaryRepository,
            FoundWordRepository foundWordRepository
    ){
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.dictionaryRepository = dictionaryRepository;
        this.foundWordRepository = foundWordRepository;
    }

    /**
     * Method Checks if word submission is valid and return Result data structure;
     *
     * @param gameId the ID of the game; may refer to a SOLO, BOT, or MULTIPLAYER game
     * @param playerId Id of player submitting the word.
     * @param rawWord word submitted by user.
     * @return Returns Result data structure filled in with required data.
     */
    @Transactional
    public Result submitWord(Integer gameId, Integer playerId, String rawWord){
        Result out = new Result();
        String word = (rawWord == null)? "": rawWord.trim().toUpperCase();
        out.normalizedWord = word;

        Game game = gameRepository.findById(gameId).orElse(null);
        //makes sure that a game exist before checking a word is possible
        if(game==null){
            out.accepted = false;
            out.reason = "GAME_NOT_FOUND";
            return out;
        }

        User player = userRepository.findById(playerId).orElse(null);
        //checks if user associated with submission is real or exist.
        if(player == null){
            out.accepted = false;
            out.reason = "PLAYER_NOT_FOUND";
            return out;
        }

        //checking if player belongs in game
        Integer p1Id = game.getPlayer1().getId();
        Integer p2Id = (game.getPlayer2() == null)? null: game.getPlayer2().getId();

        boolean isP1 = playerId.equals(p1Id);
        boolean isP2 = (p2Id != null) && playerId.equals(p2Id);
        if(!isP1 && !isP2){
            out.accepted = false;
            out.reason = "PLAYER_NOT_IN_GAME";
            return out;
        }

        Dictionary dict = dictionaryRepository.findByWord(word).orElse(null);
        if(dict==null){
            out.accepted = false;
            out.reason = "NOT_IN_DICTIONARY";
            return out;
        }
        //Check valid board
        String [][] grid = unflatten.parseStringTo4x4(game.getBoard().getBoardString());
        if(!ValidateAnswers.isValidOnBoard(word,grid)){
            out.accepted = false;
            out.reason = "NOT_ON_BOARD";
            return out;
        }

        //duplicate check
        if(foundWordRepository.existsByGame_IdAndPlayer_IdAndDictionaryWord_Id(gameId,playerId,dict.getId())){
            out.accepted = false;
            out.reason = "DUPLICATE";
            return out;
        }

        FoundWord wordPassed = new FoundWord();
        wordPassed.setGame(game);   // links word submitted to game it was submitted in (found_words.game_id = game.id)
        wordPassed.setDictionaryWord(dict); //sets what dictionary word corresponds to.
        wordPassed.setPlayer(userRepository.getReferenceById(playerId));

        //check a word must be greater than or equal to 3
        if(word.length()<3){
            out.reason = "TOO_SHORT";
            out.accepted = false;
            return out;
        }

        //avoid double submission entry error
        try {
            foundWordRepository.save(wordPassed);
        } catch (DataIntegrityViolationException e) {
            // handles race condition duplicates
            out.accepted = false;
            out.reason = "DUPLICATE";
            return out;
        }
        out.accepted = true;
        out.reason = "OK";
        out.points = dict.getPointValue();
        return out;
    }


}