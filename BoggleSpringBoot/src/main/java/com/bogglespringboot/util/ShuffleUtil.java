package com.bogglespringboot.util;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Random;

public class ShuffleUtil extends BoggleBag{
    private static final Random shuffle = new Random();
    public static final String[][] boardCreated;

    public static String[][] shuffle_board(){
        String [][] board = new String[4][4];
        for(int i = 0; i < 16 ;i++){
            String character = BoggleBag.getBag().get(shuffle.nextInt(getBag().size()));
            board[i/4][i%4] = character;

        }
        boardCreated = board;

        return board;
    }



}