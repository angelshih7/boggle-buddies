package com.bogglespringboot.util;


import com.bogglespringboot.Model.Tables.Dictionary;

public class ValidateAnswers{



    public static boolean ValidateAnswer(String wordGuessed, String[][]board, Dictionary dictionary){
        //checks correct word guessed length
        if(wordGuessed.length()<3){
            return false;
        }
        //checks correct word
        if(!existOnBoard(board,wordGuessed)){
            return false;
        }
        return true;

    }

    private static final int [][] DIRECTIONS = {
            {1, 0},//down
            {0,1},//right
            {-1,0},//up
            {0,-1},//left
            {-1,1},//diagonal up-right
            {1,1},// diagonal down-right
            {-1,-1},//diagonal up-left
            {1,-1}//diagonal down-left
            };

    private static boolean existOnBoard(String[][] board, String word){
        if(board==null || board.length == 0 || board[0].length == 0 ) return false;//might remove leater
        int rows = board.length;
        int cols = board[0].length;

        boolean [][] visited = new boolean[rows][cols];
        char first = word.charAt(0);

        for(int r = 0; r < rows; r++){
            for(int c = 0; c < cols; c++){
                String tile = board[r][c];
                if (tile == null || tile.isEmpty()) continue;

                char tileChar = Character.toUpperCase(tile.charAt(0));
                if (tileChar == first) {
                    if (dfs(board, word, r, c, 0, visited)) return true;
                }
            }
        }

        return false;
    }

    private static boolean dfs(String[][] board, String word, int r, int c, int idx, boolean[][] visisted){
        if(r < 0 || r >=board.length || c < 0 || c >= board[0].length) return false;
        if(visisted[r][c]) return false;

        String tile = board[r][c];
        if(tile == null) return false;

        char tileChar = Character.toUpperCase(tile.charAt(0));
        if (tileChar != word.charAt(idx)) return false;

        if (idx == word.length() - 1) return true;

        visisted[r][c] = true;

        for (int[] d: DIRECTIONS){
            int newRow = r + d[0];
            int newCol =  c + d[1];
            if(dfs(board,word,newRow,newCol,idx+1,visisted)){
                visisted[r][c] = false;
                return true;
            }
        }


        visisted[r][c] = false;
        return false;
    }

    private static boolean checksDictionary(Dictionary dictionary, String word){
        return true;
    }
}