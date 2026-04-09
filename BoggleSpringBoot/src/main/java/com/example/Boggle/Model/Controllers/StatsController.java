package com.example.Boggle.Model.Controllers;
import com.example.Boggle.Model.Tables;
import com.example.Boggle.repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class StatsController {

    @Autowired
    private StatsRepository statsRepository;

    @GetMapping("/{userId}/stats")
    public ResponseEntity<UserStatsDTO> getStats(@PathVariable Integer userId) {
        UserStatsDTO stats = statsRepository.getUserStats(userId);
        return ResponseEntity.ok(stats);
    }
}