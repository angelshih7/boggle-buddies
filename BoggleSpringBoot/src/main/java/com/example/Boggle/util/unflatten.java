package com.example.Boggle.util;

public class unflatten {

    public String[][] gridBoard;

    public static String [][] parseStringTo4x4 (String boardString){
        if(boardString==null){
            throw new IllegalArgumentException("boardString is null");
        }
        String [] rows = boardString.split("\\R");

        String [][] grid = new String[4][4];
        for(int r = 0; r<4; r++){
            for(int c = 0; c<4; c++){
                grid[r][c] = String.valueOf(rows[r].charAt(c));
            }
        }
        return grid;
    }
}
