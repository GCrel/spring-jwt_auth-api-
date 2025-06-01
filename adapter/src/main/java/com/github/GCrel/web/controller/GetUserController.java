package com.github.GCrel.web.controller;

import application.exceptions.UserNotFoundException;
import com.github.GCrel.web.models.UserDTO;
import com.github.GCrel.web.services.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import port.input.IGetAllUsersInput;
import port.input.IGetUserByIdInput;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class GetUserController {
    private final IGetAllUsersInput getAllUsersUseCase;
    private final IGetUserByIdInput getUserByIdUseCase;
    private final JWTService jwtService;

    @Autowired
    public GetUserController(IGetAllUsersInput getAllUsersUseCase, IGetUserByIdInput getUserByIdUseCase, JWTService jwtService) {
        this.getAllUsersUseCase = getAllUsersUseCase;
        this.getUserByIdUseCase = getUserByIdUseCase;
        this.jwtService = jwtService;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> entityList = getAllUsersUseCase.getAllUsers().stream()
                .map(UserDTO::toUserDTO).collect(Collectors.toList());
        if (entityList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(entityList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id, HttpServletRequest request) {
        String token = jwtService.extractTokenFromRequest(request);
        String currentUserRole = jwtService.extractRole(token);
        String currentUserId = jwtService.extractId(token);

        try {
            UserDTO userDTO = UserDTO.toUserDTO(getUserByIdUseCase.getUserById(id));

            if ("ADMIN".equals(currentUserRole)) {
                return ResponseEntity.ok(userDTO);
            }

            if ("USER".equals(currentUserRole)) {
                if (!id.toString().equals(currentUserId)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
                return ResponseEntity.ok(userDTO);
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
