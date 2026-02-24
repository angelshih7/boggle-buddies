package com.Backend.game_functions;

public class points{

    private static int score = 0;
    public static void  calculate_word_score(String wordOfBoggle){
        if(wordOfBoggle.length()==3 || wordOfBoggle.length()==4){
            score += 1;
        }else if(wordOfBoggle.length()==5){
            score += 2;
        }
        else if(wordOfBoggle.length()==6){
            score += 3;
        }
        else if(wordOfBoggle.length()==7){
            score += 5;
        }else if(wordOfBoggle.length()>=8){
            score += 11;
        }
    }
}