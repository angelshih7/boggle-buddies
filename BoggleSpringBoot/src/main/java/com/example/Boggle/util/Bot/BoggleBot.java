package com.example.Boggle.util.Bot;


import com.example.Boggle.Model.Tables.Board;
import com.example.Boggle.repository.FoundWordRepository;

import java.util.List;

/**
 * Represents a bot opponent in a Boggle game.
 *
 * <p>The bot selects words from the board based on its difficulty level.
 */
public class BoggleBot {

    private BotDifficulty difficulty;

    /**
     * Creates a bot with the given difficulty level.
     *
     * @param difficulty the difficulty level of the bot
     */
    public BoggleBot(BotDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    private FoundWordRepository botWords;

    /**
     * Selects words to play from the given board based on the bot's difficulty.
     *
     * @param board the current game board
     * @return a list of words chosen by the bot
     */
    public List<String> chooseWords(Board board) {

        return null;
    }



}
