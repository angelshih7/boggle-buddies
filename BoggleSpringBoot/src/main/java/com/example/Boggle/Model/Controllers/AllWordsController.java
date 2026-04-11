package com.example.Boggle.Model.Controllers;

import com.example.Boggle.Model.Tables.Board;
import com.example.Boggle.Model.Tables.Game;
import com.example.Boggle.Service.FindWordsService;
import com.example.Boggle.Service.GameService;
import com.example.Boggle.Service.TrieNodeHelper.WordCandidate;
import com.example.Boggle.repository.BoardRepository;
import com.example.Boggle.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/game")
public class AllWordsController {


    private GameService gameService;
    private FindWordsService findWordsService;

    public AllWordsController(GameService gameService, FindWordsService findWordsService){
        this.gameService = gameService;
        this.findWordsService = findWordsService;
    }


    @GetMapping("{gameId}/board/words")
    public ResponseEntity<List<WordCandidate>> getAllBoardWords(@PathVariable Integer gameId ){
        Board board =  gameService.getBoard(gameId);
        String boardString = board.getBoardString();
        List<WordCandidate> words = findWordsService.findValidWords(boardString);
        return ResponseEntity.ok(words);
    }


}
