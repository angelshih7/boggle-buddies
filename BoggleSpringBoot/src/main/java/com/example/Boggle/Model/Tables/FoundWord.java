package com.example.Boggle.Model.Tables;


import jakarta.persistence.*;

import java.time.LocalDateTime;

/*
Represents a persisted Boggle found word row in the found_word table, including found_word layout and creation time.
found_word table stores the game session words found during the boggle game.
 */
@Entity
@Table(name="found_words",
        indexes = {@Index(name = "fk_found_game", columnList = "game_id"),
                @Index(name="fk_found_dictionary",columnList = "dictionary_word_id")
        }
        , uniqueConstraints =
    @UniqueConstraint(name = "unique_word_per_player_per_game",
            columnNames = {"player_id",
                            "game_id",
                            "dictionary_word_id"
            }
    )
)
public class FoundWord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name="player_id", foreignKey = @ForeignKey(name = "fk_found_player"))
    private User player;

    @ManyToOne(optional = false)
    @JoinColumn(name="game_id", foreignKey = @ForeignKey(name="fk_found_game"))
    private Game game;

    @ManyToOne(optional = false)
    @JoinColumn(name="dictionary_word_id", foreignKey=@ForeignKey(name="fk_found_dictionary"))
    private Dictionary dictionaryWord;

    @Column(name="found_at",nullable = false,insertable = false,updatable = false)
    private LocalDateTime foundAt;

    public User getPlayer(){
        return player;
    }
    public Integer getId(){
        return id;
    }
    public Game getGame(){
        return game;
    }

    public Dictionary getDictionaryWord() {
        return dictionaryWord;
    }
    public LocalDateTime getFoundAt() {
        return foundAt;
    }

    public void setPlayer(User player) {
        this.player = player;
    }

    public void setDictionaryWord(Dictionary dictionaryWord) {
        this.dictionaryWord = dictionaryWord;
    }

    public void setGame(Game game) {
        this.game = game;
    }

}
