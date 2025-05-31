package com.github.GCrel.data.jpa;

import com.github.GCrel.data.models.UserEntity;
import models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IJPAUserRepository extends CrudRepository<UserEntity, UUID> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Optional<UserEntity> findUserByEmailAndPassword(String email, String password);

    Optional<UserEntity> findUserEntityById(UUID id);
}
