package com.bogglespringboot.repository;

import com.bogglespringboot.Model.Tables.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Integer> {
}