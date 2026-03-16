package com.example.Boggle.Model.Tables;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Entity representing a dictionary word that can be found during gameplay.
 *
 * <p>Each dictionary entry stores the word itself, its point value, and the
 * time the row was created.
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

    /**
     * Creates an empty dictionary entity.
     */
    public Dictionary(){}

    /**
     * Returns the dictionary row ID.
     *
     * @return the dictionary ID
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the stored word.
     *
     * @return the dictionary word
     */
    public String getWord(){
        return word;
    }

    /**
     * Returns the point value assigned to the word.
     *
     * @return the point value
     */
    public int getPointValue() {
        return pointValue;
    }

    /**
     * Returns the time the dictionary row was created.
     *
     * @return the creation timestamp
     */
    public LocalDateTime getCreatedAt(){
        return createdAt;
    }
}
