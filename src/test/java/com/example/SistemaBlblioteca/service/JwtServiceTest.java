package com.example.SistemaBlblioteca.service;

import com.example.SistemaBlblioteca.exceptions.auth.InvalidTokenException;
import com.example.SistemaBlblioteca.security.jwt.JwtService;
import com.example.SistemaBlblioteca.security.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(
                "secret-key"
        );
    }

    @Test
    void generateToken_ShouldGenerateTokenWithCorrectSubject() {
        User user = new User();
        user.setEmail("user@email.com");

        String token = jwtService.generateToken(user);
        String subject = jwtService.validateToken(token);

        assertNotNull(token);
        assertEquals("user@email.com", subject);
    }

    @Test
    void validateToken_ShouldReturnSubject_WhenTokenIsValid() {
        User user = new User();
        user.setEmail("user@email.com");

        String token = jwtService.generateToken(user);
        String subject = jwtService.validateToken(token);

        assertEquals(user.getEmail(), subject);
    }

    @Test
    void validateToken_ShouldThrowException_WhenTokenIsInvalid() {
        String invalidToken = "invalid-token";

        assertThrows(InvalidTokenException.class, () -> jwtService.validateToken(invalidToken));
    }

    @Test
    void validateToken_ShouldThrowException_WhenTokenWasSignedWithDifferentSecretKey() {
        JwtService anotherJwtService = new JwtService("another-secret-key");

        User user = new User();
        user.setEmail("user@email.com");

        String token = anotherJwtService.generateToken(user);
        assertThrows(InvalidTokenException.class, () -> jwtService.validateToken(token));
    }
}
