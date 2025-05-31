package com.github.GCrel.web.controller;

import application.exceptions.UserAlreadyExistsExceptions;
import com.github.GCrel.web.controller.vo.LoginRequest;
import com.github.GCrel.web.models.UserDTO;
import com.github.GCrel.web.services.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import port.input.ILoginUserInput;
import port.input.IRegisterUserInput;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final ILoginUserInput loginUserUseCase;
    private final IRegisterUserInput registerUserUseCase;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;

    @Autowired
    public AuthController(ILoginUserInput loginUserUseCase, IRegisterUserInput registerUserUseCase, PasswordEncoder passwordEncoder, JWTService jwtService) {
        this.loginUserUseCase = loginUserUseCase;
        this.registerUserUseCase = registerUserUseCase;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody UserDTO userDTO) {
        try {
            // Encode the password before registering the user
            userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            UserDTO registeredUser = UserDTO.toUserDTO(registerUserUseCase.registerUser(userDTO.toUser()));
            return ResponseEntity.ok(registeredUser);
        } catch (UserAlreadyExistsExceptions e) {

            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        try {
            UserDTO loggedInUser = UserDTO.toUserDTO(loginUserUseCase.loginUser(loginRequest.email(), loginRequest.password()));
            String token = jwtService.generateToken(loggedInUser.toUser());
            return ResponseEntity.ok(token);
        } catch (UserAlreadyExistsExceptions e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
