package com.example.Boggle;


import com.example.Boggle.Model.Controllers.UserController;
import com.example.Boggle.Model.Tables.User;
import com.example.Boggle.Security.PasswordUtil;
import com.example.Boggle.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.ResponseStatusException;

import java.net.http.HttpClient;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoBean
public class UnitAPIUserTests {

    private UserRepository userRepository;
    private UserController userController;

    @BeforeEach
    void setup(){
        userRepository = mock(UserRepository.class);
        userController = new UserController(userRepository);
    }

    @Test
    void testCreateAccountNewUser(){
       UserController.RegisterRequest req = new UserController.RegisterRequest();
       req.username = "diego9";
       req.email = "diego@test.com";
       req.password = "Secret123";

       when(userRepository.existsByUsername("diego9")).thenReturn(false);
       when(userRepository.findByEmail("diego@test.com")).thenReturn(Optional.empty());

       User savedUser = new User("diego9","diego@test.com","Secret123");
       when(userRepository.save(savedUser)).thenReturn(savedUser);

       UserController.UserResponse response =  userController.register(req);
       savedUser.setGuest(false);

       assertNotNull(response);
       assertEquals("diego9",response.username);
       assertEquals("diego@test.com",response.email);

       verify(userRepository).existsByUsername("diego9");
       verify(userRepository).findByEmail("diego@test.com");
       verify(userRepository).save(any(User.class));
    }

    @Test
    void testCreateGuestNew(){
        UserController.GuestRequest req = new UserController.GuestRequest();
        req.username = "Guest";

        when(userRepository.existsByUsername("Guest")).thenReturn(false);
        User savedGuest = new User("Guest", "guest_123@test.com","Hashcode");
        savedGuest.setGuest(true);

        when(userRepository.save(any(User.class))).thenReturn(savedGuest);

        UserController.UserResponse response = userController.guest(req);

        assertNotNull(response);
        assertEquals("Guest",response.username);
        assertEquals("guest_123@test.com",response.email);

        verify(userRepository).existsByUsername("Guest");
        verify(userRepository).save(any(User.class));
    }
    @Test
    void testloginToExistUser(){
        UserController.LoginRequest req = new UserController.LoginRequest();
        req.username = "Diego9";
        req.password = "MyPassword123";

        String hashedPassword = PasswordUtil.hash("MyPassword123");
        User loginUser = new User("Diego9","diego@test.com",hashedPassword);
        loginUser.setGuest(false);

        when(userRepository.findByUsername("Diego9")).thenReturn(Optional.of(loginUser));

        UserController.UserResponse response = userController.login(req);

        assertNotNull(response);
        assertEquals("Diego9", response.username);
        assertEquals("diego@test.com", response.email);

        verify(userRepository).findByUsername("Diego9");
    }

    @Test
    void testRegisterRepeatedUsername(){
        UserController.RegisterRequest req = new UserController.RegisterRequest();
        req.username = "Diego9";
        req.email = "diego@test.com";
        req.password = "Secret123";

        when(userRepository.existsByUsername("Diego9")).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> userController.register(req));

    }

    @Test
    void testRegisterRepeatedEmail(){
        UserController.RegisterRequest req = new UserController.RegisterRequest();
        req.username = "Diego9";
        req.email = "diego@test.com";
        req.password = "Secret123";

        String hashedPassword = PasswordUtil.hash("MyPassword123");
        User existingUser = new User("OtherUser","diego@test.com",hashedPassword);
        existingUser.setGuest(false);

        when(userRepository.existsByUsername("Diego9")).thenReturn(false);
        when(userRepository.findByEmail("diego@test.com")).thenReturn(Optional.of(existingUser));


        assertThrows(ResponseStatusException.class, () -> userController.register(req));
        verify(userRepository).existsByUsername("Diego9");
        verify(userRepository).findByEmail("diego@test.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testLoginWrongUsername(){
        UserController.LoginRequest req = new UserController.LoginRequest();
        req.username = "WrongUser";
        req.password = "CorrectPassword";

        ResponseStatusException Ex = assertThrows(ResponseStatusException.class,()->userController.login(req));

        assertEquals(HttpStatus.UNAUTHORIZED,Ex.getStatusCode());

        verify(userRepository).findByUsername("WrongUser");

    }

    @Test
    void testLoginWrongPassword(){
        UserController.LoginRequest req = new UserController.LoginRequest();
        req.username = "Diego9";
        req.password = "WrongPassword";

        String hashedPassword = PasswordUtil.hash("CorrectPassword");
        User loginUser = new User("Diego9","diego@test.com",hashedPassword);
        loginUser.setGuest(false);

        when(userRepository.findByUsername("Diego9"))
                .thenReturn(Optional.of(loginUser));

        assertThrows(ResponseStatusException.class,()->{userController.login(req);
        });

        verify(userRepository).findByUsername("Diego9");

    }

}
