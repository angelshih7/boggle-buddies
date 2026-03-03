package com.bogglespringboot.repository;

import com.bogglespringboot.Model.Tables.Dictionary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DictionaryRepository extends JpaRepository<Dictionary, Integer> {
    Optional<Dictionary> findByWord(String word);
}