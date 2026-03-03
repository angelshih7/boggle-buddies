package com.bogglespringboot.repository;

import com.bogglespringboot.Session.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findBySessionCode(String sessionCode);
}
