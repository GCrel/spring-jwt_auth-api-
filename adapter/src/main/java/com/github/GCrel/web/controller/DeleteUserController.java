package com.github.GCrel.web.controller;

import application.exceptions.UserNotFoundException;
import com.github.GCrel.web.models.UserDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import port.input.IDeleteUserInput;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class DeleteUserController {
    private final IDeleteUserInput deleteUserUseCase;

    @Autowired
    public DeleteUserController(IDeleteUserInput deleteUserUseCase) {
        this.deleteUserUseCase = deleteUserUseCase;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserDTO> deleteUserById(@PathVariable UUID id, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(UserDTO.toUserDTO(deleteUserUseCase.deleteUser(id)));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
