package com.bogglespringboot.util;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Random;

public class ShuffleUtil extends BoggleBag {
    private static final Random shuffle = new Random();

    public static class GeneratedBoard {
        public final String[][] boardGrid;
        public final String flattened;

        public GeneratedBoard(String[][] grid, String flattened) {
            this.boardGrid = grid;
            this.flattened = flattened;
        }
    }

    public static GeneratedBoard shuffle_board() {
        String[][] board = new String[4][4];
        for (int i = 0; i < 16; i++) {
            String character = BoggleBag.getBag().get(shuffle.nextInt(getBag().size()));
            board[i / 4][i % 4] = character;

        }
        String flat = flatten(board);
        return new GeneratedBoard(board, flat);
    }

    public static String flatten(String[][] grid) {
        StringBuilder flattenGrid = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                flattenGrid.append(grid[i][j]);
            }
            if (i < 3) {
                flattenGrid.append('\n');
            }

        }
        return flattenGrid.toString();
    }





}