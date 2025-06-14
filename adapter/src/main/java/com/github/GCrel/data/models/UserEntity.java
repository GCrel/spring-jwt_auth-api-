package com.github.GCrel.data.models;

import jakarta.persistence.*;
import lombok.*;
import models.User;
import models.vo.Role;

import java.util.UUID;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class UserEntity {
    @Id
    private UUID id;
    private String username;
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;

    public static UserEntity toUserEntity(User user) {
        return UserEntity.builder()
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
