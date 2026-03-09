package com.bogglespringboot.repository;

import com.bogglespringboot.Model.Tables.Game;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Provides standard Spring Data JPA operations such as creating,
 * retrieving, updating, and deleting game records for Game entities.
 * The repository uses:
 * - Game as the entity type
 * - Integer as the primary key type
 */
public interface GameRepository extends JpaRepository<Game, Integer> {
}