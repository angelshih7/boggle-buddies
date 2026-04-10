package com.example.Boggle.Service;


import com.example.Boggle.Service.TrieNodeHelper.DictionaryTrieService;
import com.example.Boggle.Service.TrieNodeHelper.TrieNode;
import com.example.Boggle.Service.TrieNodeHelper.WordCandidate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for {@link FindWordsService}.
 *
 * <p>These tests focus on confirming correct
 * Behavior from DFS traversal and the correct word being returned
 */
@MockitoBean
public class FindWordsServiceTest {

    private DictionaryTrieService dictionaryTrieService;
    private FindWordsService findWordsService;

    /**
     *
     */
    @BeforeEach
    void setUp(){
        dictionaryTrieService = mock(DictionaryTrieService.class);
        findWordsService = new FindWordsService(dictionaryTrieService);
    }


    /**
     * Build simple trie to avoid depending on other classes.
     *
     * @param word the word to add
     * @param points the point value of the word
     * @return root trie node
     */
    private TrieNode buildTrieForSingleWord(String word, int points){
        TrieNode root = new TrieNode();
        TrieNode current = root;

        for(char ch: word.toUpperCase().toCharArray()){
            root.children.putIfAbsent(ch,current);
            current = current.children.get(ch);
        }

        current.isWord = true;
        current.pointValue = points;
        current.word = word.toUpperCase();

        return current;
    }

    /**
     * Test check if the resulting foundWords Map contains all possible words from board;
     */
    @Test
    void FindingExistingWords(){
        TrieNode root = buildTrieForSingleWord("CAT",2);

        String [][] board = {
                {"C", "A", "T", "X"},
                {"X", "X", "X", "X"},
                {"X", "X", "X", "X"},
                {"X", "X", "X", "X"}
        };

        boolean [][] visisted = new boolean[4][4];
        Map<String, WordCandidate> foundWords = new HashMap<>();

        findWordsService.dfs(board,0,0,root,visisted,foundWords);

        assertEquals(1, foundWords.size());
        assertTrue(foundWords.containsKey("CAT"));
        assertEquals("CAT", foundWords.get("CAT").getWord());
        assertEquals(2, foundWords.get("CAT").getPoints());
    }

}
