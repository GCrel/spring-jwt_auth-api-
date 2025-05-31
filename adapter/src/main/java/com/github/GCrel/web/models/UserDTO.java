package com.github.GCrel.web.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import models.User;
import models.vo.Role;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class UserDTO {
    private UUID id;
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;
    @Email
    private String email;
    private String password;
    private Role role;

    public static UserDTO toUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .role(user.getRole())
                .build();
    }

    public User toUser() {
        User user = new User(username, email, password, role);
        user.setId(id);
        return user;
    }
}
