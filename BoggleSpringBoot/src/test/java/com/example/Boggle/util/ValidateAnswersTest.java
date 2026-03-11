package com.example.Boggle.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidateAnswersTest {

    private final String[][] board = {
            {"C", "A", "T", "S"},
            {"D", "O", "G", "E"},
            {"B", "I", "R", "D"},
            {"F", "I", "S", "H"}
    };

    @Test
    void validWordOnBoardReturnsTrue() {
        assertTrue(ValidateAnswers.isValidOnBoard("CAT", board));
    }

    @Test
    void diagonalWordOnBoardReturnsTrue() {
        assertTrue(ValidateAnswers.isValidOnBoard("COG", board));
    }

    @Test
    void wordNotOnBoardReturnsFalse() {
        assertFalse(ValidateAnswers.isValidOnBoard("COW", board));
    }

    @Test
    void nullWordReturnsFalse() {
        assertFalse(ValidateAnswers.isValidOnBoard(null, board));
    }
}