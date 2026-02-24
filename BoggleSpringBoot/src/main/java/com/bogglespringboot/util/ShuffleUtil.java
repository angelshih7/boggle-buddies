package com.Backend.game_functions;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Random;

public class shuffle extends Boggle_bag{
    private static final Random shuffle = new Random();


    public static String[][] shuffle_board(){
        String [][] board = new String[4][4];
        for(int i = 0; i < 16 ;i++){
            String character = Boggle_bag.getBag().get(shuffle.nextInt(getBag().size()));
            board[i/4][i%4] = character;

        }

        return board;
    }



}