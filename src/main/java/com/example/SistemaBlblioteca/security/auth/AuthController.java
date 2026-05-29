package com.example.SistemaBlblioteca.security.auth;

import com.example.SistemaBlblioteca.dto.messageDTO.MessageResponseDTO;
import com.example.SistemaBlblioteca.security.auth.dto.LoginRequestDTO;
import com.example.SistemaBlblioteca.security.auth.dto.LoginResponseDTO;
import com.example.SistemaBlblioteca.security.auth.dto.RegisterDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<MessageResponseDTO> register(@Valid @RequestBody RegisterDTO request){
        authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponseDTO("User registered successfully!"));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request){
        return ResponseEntity.ok(authService.login(request));
    }
}
