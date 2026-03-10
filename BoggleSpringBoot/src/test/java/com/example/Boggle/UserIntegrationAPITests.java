package com.example.Boggle;


import com.example.Boggle.Model.Tables.*;
import com.example.Boggle.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito.*;


import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import com.example.Boggle.Model.Controllers.UserController;

public class UserAPITests {



    @Test
    void registerCreateUserSuccesfully(){
        UserRepository userRepository = mock(UserRepository.class);
        UserController userController = new UserController(userRepository);

        UserController.RegisterRequest req = new UserController.RegisterRequest();

        req.username = "diego9";
        req.email = "diego@test.com";
        req.password="Secret123";
        when(userRepository.existsByUsername("diego9")).thenReturn(false);
        when(userRepository.findByEmail("diego@test.com")).thenReturn(Optional.empty());

        User savedUser = new User("diego9","diego@test.com","someStoredHash");
        savedUser.setGuest(false);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserController.UserResponse response = userController.register(req);

        assertEquals("diego9",response.username);
        assertEquals("diego@test.com", response.email);

    }
    void registerCreateGuest(){

    }

    void registerCreateUser(){

    }
}
