package com.example.Boggle.repository;

import com.example.Boggle.Model.Tables.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Integer> {
}