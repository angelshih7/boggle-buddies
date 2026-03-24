package com.example.Boggle.util.Bot;


import com.example.Boggle.Model.Tables.Board;
import com.example.Boggle.repository.FoundWordRepository;

import java.util.List;

public class BoggleBot {

    private BotDifficulty difficulty;


    public void BoggleBot(BotDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    private FoundWordRepository botWords;
    public List<String> chooseWords(Board board) {

        return null;
    }



}
