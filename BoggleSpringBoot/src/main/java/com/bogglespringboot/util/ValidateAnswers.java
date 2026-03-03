package com.bogglespringboot.util

import
public class ValidateAnswers{



    public static boolean ValidateAnswer(String wordGuessed, String[][]board, Dictionary dictionary){
        //checks correct word guessed length
        if(wordGuessed.length()<3){
            return false;
        }
        //checks correct word


    }

    private static final int [][] DIRECTIONS = {
            {1, 0},//down
            {0,1),//right
            {-1,0),//up
            {0,-1},//left
            {-1,1},//diagonal up-right
            {1,1},// diagonal down-right
            {-1,-1},//diagonal up-left
            {1,-1}//diagonal down-left
            ]
};

    private static NeighbhorGenerator(String[][] board){
        if(board==null || board.length == 0 || board[0].length == 0 ) return false;//might remove leater
        int row_length = board.length();
        int column_length = board[0].length();

        boolean [][] visited = [row_length][column_length]




    }



}