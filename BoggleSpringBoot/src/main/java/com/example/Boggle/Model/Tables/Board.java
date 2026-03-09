package com.example.Boggle.Model.Tables;


import jakarta.persistence.*;

import java.time.LocalDateTime;

/*
Represents a persisted Boggle board row in the boards table, including board layout and creation time.
 */

@Entity
@Table(name = "boards")
public class Board {
    //constructor of board
    public Board(){}
    @Id
    @Column(name = "board_id",length = 50, nullable = false)
    private String boardId;

    //Stores board letters and layout
    @Column(name="board_string",columnDefinition = "text", nullable = false)
    private String boardString;


    public String getBoardId() { return boardId; }
    public void setBoardId(String boardId) { this.boardId = boardId; }

    public String getBoardString() { return boardString; }
    public void setBoardString(String boardString) { this.boardString = boardString; }


}
