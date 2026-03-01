package com.bogglespringboot.Model.Tables;

import jakarta.persistence.*;

import java.time.LocalDateTime;
/*
Represents a persisted Boggle dictionary row in the dictionary table, including dictionary layout and creation time.
Every word has an id and a point value.
 */
@Entity
@Table(name="dictionary")
public class Dictionary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @Column(name="word",length = 100,nullable = false, unique = true)
    private String word;

    @Column(name="point_value", nullable = false)
    private Integer pointValue;

    @Column(name="created_at",insertable = false,updatable = false)
    private LocalDateTime createdAt;

    public Dictionary(){}
    public int getId() {
        return id;
    }
    public String getWord(){
        return word;
    }
    public int getPointValue() {
        return pointValue;
    }
    public LocalDateTime getCreatedAt(){
        return createdAt;
    }

}
