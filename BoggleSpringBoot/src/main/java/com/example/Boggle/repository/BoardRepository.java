package com.example.Boggle.repository;

import com.example.Boggle.Model.Tables.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, String> {
}