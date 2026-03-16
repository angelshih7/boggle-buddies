package com.example.Boggle.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShuffleUtilTest {

    @Test
    void getBagContainsExpectedWeightedSize() {
        List<String> bag = BoggleBag.getBag();

        assertEquals(98, bag.size());
        assertEquals(12, bag.stream().filter("E"::equals).count());
        assertEquals(1, bag.stream().filter("Q"::equals).count());
    }

    @Test
    void shuffleBoardReturnsFourByFourBoardAndFlattenedString() {
        ShuffleUtil.GeneratedBoard generatedBoard = ShuffleUtil.shuffledBoard();

        assertNotNull(generatedBoard);
        assertNotNull(generatedBoard.boardGrid);
        assertEquals(4, generatedBoard.boardGrid.length);

        for (String[] row : generatedBoard.boardGrid) {
            assertEquals(4, row.length);
            for (String cell : row) {
                assertNotNull(cell);
                assertFalse(cell.isBlank());
                assertTrue(BoggleBag.getBag().contains(cell));
            }
        }

        String[] rows = generatedBoard.flattened.split("\\R");
        assertEquals(4, rows.length);
        for (String row : rows) {
            assertEquals(4, row.length());
        }
    }

    @Test
    void flattenFormatsBoardAsExpected() {
        String[][] grid = {
                {"A", "B", "C", "D"},
                {"E", "F", "G", "H"},
                {"I", "J", "K", "L"},
                {"M", "N", "O", "P"}
        };

        String flattened = ShuffleUtil.flatten(grid);

        assertEquals("ABCD\nEFGH\nIJKL\nMNOP", flattened);
    }
}