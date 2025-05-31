package com.github.GCrel.web.controller;

import application.exceptions.UserNotFoundException;
import com.github.GCrel.web.models.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import port.input.IGetAllUsersInput;
import port.input.IGetUserByIdInput;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class GetUserController {
    private final IGetAllUsersInput getAllUsersUseCase;
    private final IGetUserByIdInput getUserByIdUseCase;

    @Autowired
    public GetUserController(IGetAllUsersInput getAllUsersUseCase, IGetUserByIdInput getUserByIdUseCase) {
        this.getAllUsersUseCase = getAllUsersUseCase;
        this.getUserByIdUseCase = getUserByIdUseCase;
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
    public ResponseEntity<UserDTO> getUserById(@PathVariable("id") UUID id) {
        try {
            UserDTO userDTO = UserDTO.toUserDTO(getUserByIdUseCase.getUserById(id));
            return ResponseEntity.ok(userDTO);
        }catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
