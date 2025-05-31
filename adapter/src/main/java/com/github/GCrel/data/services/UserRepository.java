package com.github.GCrel.data.services;

import application.exceptions.InvalidCredentialsException;
import com.github.GCrel.data.jpa.IJPAUserRepository;
import com.github.GCrel.data.models.UserEntity;
import com.github.GCrel.data.services.exception.DataBaseException;
import models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import port.output.IUserRepository;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
public class UserRepository implements IUserRepository {
    private final IJPAUserRepository jpaUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserRepository(IJPAUserRepository jpaUserRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.jpaUserRepository = jpaUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaUserRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaUserRepository.existsByUsername(username);
    }

    @Override
    public User saveUser(User user) {
        try {
            return jpaUserRepository.save(UserEntity.toUserEntity(user)).toUser();
        } catch (DataBaseException e) {
            throw new DataBaseException("Could not save user:" + e.getMessage());
        }
    }

    @Override
    public Optional<User> validateUser(String email, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        Optional<UserEntity> userEntityOpt = jpaUserRepository.findByEmail(email);
        return userEntityOpt.map(UserEntity::toUser);
    }

    @Override
    public List<User> findAllUsers() {
        Iterable<UserEntity> taskEntities = jpaUserRepository.findAll();
        return StreamSupport.stream(taskEntities.spliterator(), false)
                .map(UserEntity::toUser)
                .toList();
    }

    @Override
    public Optional<User> findUserById(UUID id) {
        Optional<UserEntity> taskEntityOptional = jpaUserRepository.findById(id);
        return taskEntityOptional.map(UserEntity::toUser);
    }

    @Override
    public User updateUser(User user) {
        UserEntity newTaskEntity = UserEntity.toUserEntity(user);
        UserEntity updated = jpaUserRepository.save(newTaskEntity);
        return updated.toUser();
    }

    @Override
    public Optional<User> deleteUser(UUID id) {
        UserEntity userEntity = jpaUserRepository.findById(id)
                .orElseThrow(() -> new DataBaseException("Task with ID " + id + " not found."));
        jpaUserRepository.deleteById(id);
        return Optional.of(userEntity.toUser());
    }
}
