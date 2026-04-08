package com.example.Boggle.Service;

import com.example.Boggle.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class GuestDeletionService {
    private UserRepository userRepository;

    public GuestDeletionService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Transactional
    @Scheduled(fixedRate = 3600000)
    public void removeExpiredGuest(){
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        int deleted = userRepository.deleteExpiredGuests(cutoff);
        System.out.println("[GuestCleanup] Deleted " + deleted + " expired guest(s)");

    }

}