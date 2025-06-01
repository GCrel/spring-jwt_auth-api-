package com.github.GCrel.web.controller;

import com.github.GCrel.web.controller.vo.UpdateRequest;
import com.github.GCrel.web.models.UserDTO;
import com.github.GCrel.web.services.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import port.input.IUpdateUserInput;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UpdateUserController {
    private final IUpdateUserInput updateUserUseCase;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UpdateUserController(IUpdateUserInput updateUserUseCase, JWTService jwtService, PasswordEncoder passwordEncoder) {
        this.updateUserUseCase = updateUserUseCase;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @PutMapping("{id}")
    public ResponseEntity<UpdateRequest> updateUser(@PathVariable UUID id, @RequestBody UserDTO userDTO, HttpServletRequest request) {
        String token = jwtService.extractTokenFromRequest(request);
        String currentUserRole = jwtService.extractRole(token);
        String currentUserId = jwtService.extractId(token);

        try {

            if (userDTO.getPassword() != null) {
                userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            }

            if ("ADMIN".equals(currentUserRole)) {
                if (id.toString().equals(currentUserId)) {
                    return ResponseEntity.ok(getUpdateRequest(id, userDTO));
                } else {
                    updateUserUseCase.updateUser(id, userDTO.toUser());
                    return ResponseEntity.ok().build();
                }
            }

            if ("USER".equals(currentUserRole)) {
                if (!id.toString().equals(currentUserId)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
                userDTO.setRole(null);
                return ResponseEntity.ok(getUpdateRequest(id, userDTO));
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private UpdateRequest getUpdateRequest(UUID id, UserDTO userDTOResponse) {
        UserDTO userDTO = UserDTO.toUserDTO(updateUserUseCase.updateUser(id, userDTOResponse.toUser()));
        return new UpdateRequest(
                userDTO,
                jwtService.generateToken(userDTO.toUser())
        );
    }
}
