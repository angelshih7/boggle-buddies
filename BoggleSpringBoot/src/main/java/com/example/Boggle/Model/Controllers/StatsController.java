package com.example.Boggle.Model.Controllers;

import com.example.Boggle.repository.StatsRepository;
import com.example.Boggle.repository.StatsRepository.UserStatsProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class StatsController {

    @Autowired
    private StatsRepository statsRepository;

    @GetMapping("/{userId}/stats")
    public ResponseEntity<UserStatsProjection> getStats(@PathVariable Integer userId) {
        UserStatsProjection stats = statsRepository.getUserStats(userId);

        if (stats == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(stats);
    }
}