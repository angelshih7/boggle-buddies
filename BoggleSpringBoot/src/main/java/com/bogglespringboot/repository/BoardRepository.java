package com.bogglespringboot.repository;

import com.bogglespringboot.Model.Tables.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, String> {
}