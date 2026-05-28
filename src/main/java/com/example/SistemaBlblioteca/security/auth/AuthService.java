package com.example.SistemaBlblioteca.security.auth;

import com.example.SistemaBlblioteca.exceptions.auth.EmailAlreadyRegisteredException;
import com.example.SistemaBlblioteca.security.auth.dto.LoginRequestDTO;
import com.example.SistemaBlblioteca.security.auth.dto.LoginResponseDTO;
import com.example.SistemaBlblioteca.security.auth.dto.RegisterDTO;
import com.example.SistemaBlblioteca.security.jwt.JwtService;
import com.example.SistemaBlblioteca.security.user.Role;
import com.example.SistemaBlblioteca.security.user.User;
import com.example.SistemaBlblioteca.security.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public void register(RegisterDTO registerDTO) {
        if(userRepository.existsByEmail(registerDTO.email())) {
            throw new EmailAlreadyRegisteredException("Email:" +registerDTO.email()+ " already registered");
        }

        User user = new User();
        user.setName(registerDTO.name());
        user.setEmail(registerDTO.email());
        user.setPassword(passwordEncoder.encode(registerDTO.password()));
        user.setRole(Role.ROLE_USER);

        userRepository.save(user);
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.email(), request.password()));

            User user = (User) authentication.getPrincipal();

            String token = jwtService.generateToken(user);

            return new LoginResponseDTO(token);
        }
        catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid email or password");
        }
    }
}
