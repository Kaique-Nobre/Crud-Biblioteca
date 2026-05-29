package com.example.SistemaBlblioteca.controller;

import com.example.SistemaBlblioteca.exceptions.auth.EmailAlreadyRegisteredException;
import com.example.SistemaBlblioteca.security.auth.AuthController;
import com.example.SistemaBlblioteca.security.auth.AuthService;
import com.example.SistemaBlblioteca.security.auth.dto.LoginRequestDTO;
import com.example.SistemaBlblioteca.security.auth.dto.LoginResponseDTO;
import com.example.SistemaBlblioteca.security.auth.dto.RegisterDTO;
import com.example.SistemaBlblioteca.security.jwt.JwtAuthenticationFilter;
import com.example.SistemaBlblioteca.security.jwt.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.example.SistemaBlblioteca.util.RegisterCreator.createRegister;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void register_RegisterUser_WhenSuccessfully() throws Exception {
        RegisterDTO register = createRegister();

        doNothing().when(authService).register(register);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
        verify(authService).register(any(RegisterDTO.class));
    }

    @Test
    void register_ShouldReturnBadRequest_WhenEmailIsBlank() throws Exception {
        RegisterDTO register = new RegisterDTO("username", "", "password");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isBadRequest());
        verify(authService, never()).register(any(RegisterDTO.class));
    }

    @Test
    void register_ShouldReturnConflict_WhenEmailIsAlreadyRegistered() throws Exception {
        RegisterDTO register = createRegister();

        doThrow(new EmailAlreadyRegisteredException(
                "Email already registered")).when(authService).register(any(RegisterDTO.class));

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isConflict());
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO("user@email.com", "password");
        LoginResponseDTO loginResponse = new LoginResponseDTO("jwt-token");

        when(authService.login(any(LoginRequestDTO.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
        verify(authService).login(any(LoginRequestDTO.class));
    }

    @Test
    void login_ShouldReturnBadRequest_WhenEmailIsBlank() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO("", "password");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        verify(authService, never()).login(any(LoginRequestDTO.class));
    }

    @Test
    void login_ShouldReturnUnauthorized_WhenCredentialsAreInvalid() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO("wrongEmail@email.com", "wrong-password");

        doThrow(new BadCredentialsException("Bad credentials")).when(authService).login(any(LoginRequestDTO.class));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
