package com.bogglespringboot.Model.Tables;


import jakarta.persistence.*;
import org.springframework.context.annotation.Primary;

import java.time.LocalDateTime;

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
    private Users player;

    @ManyToOne(optional = false)
    @JoinColumn(name="game_id", foreignKey = @ForeignKey(name="fk_found_game"))
    private Games game;

    @ManyToOne(optional = false)
    @JoinColumn(name="dictionary_word_id", foreignKey=@ForeignKey(name="fk_found_dictionary"))
    private Dictionary dictionaryWord;

    @Column(name="found_at",nullable = false,insertable = false,updatable = false)
    private LocalDateTime foundAt;


}
