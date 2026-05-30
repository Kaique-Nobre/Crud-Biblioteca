package com.example.SistemaBlblioteca.service;

import com.example.SistemaBlblioteca.exceptions.auth.EmailAlreadyRegisteredException;
import com.example.SistemaBlblioteca.security.auth.AuthService;
import com.example.SistemaBlblioteca.security.auth.dto.LoginRequestDTO;
import com.example.SistemaBlblioteca.security.auth.dto.LoginResponseDTO;
import com.example.SistemaBlblioteca.security.auth.dto.RegisterDTO;
import com.example.SistemaBlblioteca.security.jwt.JwtService;
import com.example.SistemaBlblioteca.security.user.User;
import com.example.SistemaBlblioteca.security.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.example.SistemaBlblioteca.util.RegisterCreator.createRegister;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_ShouldSaveUser_WhenSuccessfully() throws Exception {
        RegisterDTO register = createRegister();

        when(userRepository.existsByEmail(register.email())).thenReturn(false);
        when(passwordEncoder.encode(register.password())).thenReturn("encoded-password");

        authService.register(register);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals(register.name(), savedUser.getName());
        assertEquals(register.email(), savedUser.getEmail());
        assertEquals("encoded-password", savedUser.getPassword());
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() throws Exception {
        RegisterDTO register = createRegister();

        when(userRepository.existsByEmail(register.email())).thenReturn(true);

        assertThrows(EmailAlreadyRegisteredException.class, () -> authService.register(register));

        verify(userRepository, never()).save(any());
    }

    @Test
    void login_ShouldReturnToken_WhenSuccessfully() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO("user@email.com", "password");

        User user = new User();
        user.setEmail(request.email());

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager
                .authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(authentication.getPrincipal()).thenReturn(user);

        when(jwtService.generateToken(any())).thenReturn("jwt-token");

        LoginResponseDTO token = authService.login(request);

        assertNotNull(token);
        assertEquals("jwt-token", token.token());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(any());
    }
}
