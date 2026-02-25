package com.bogglespringboot.Model.Tables;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "boards")
public class Board {
    @Id
    @Column(name = "board_id",length = 50, nullable = false)
    private String boardId;

    //Stores board letters and layout
    @Column(name="board_string",columnDefinition = "text", nullable = false)
    private String boardString;

    @Column(name="created_at",nullable = false,updatable = false)
    private LocalDateTime createdAt;

    public Board(){}

    public String getBoardId() { return boardId; }
   public void setBoardId(String boardId) { this.boardId = boardId; }

    public String getBoardString() { return boardString; }
    public void setBoardString(String boardString) { this.boardString = boardString; }

    public LocalDateTime getCreatedAt() { return createdAt; }

}
