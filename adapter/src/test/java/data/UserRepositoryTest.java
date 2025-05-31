package data;

import com.github.GCrel.data.jpa.IJPAUserRepository;
import com.github.GCrel.data.models.UserEntity;
import com.github.GCrel.data.services.UserRepository;
import com.github.GCrel.data.services.exception.DataBaseException;
import models.User;
import models.vo.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Mock
    private IJPAUserRepository jpaUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserRepository userRepository;

    private final User user = new User("username", "email@example.com", "encodedPassword", Role.USER);

    @Test
    void testExistsByEmail() {
        when(jpaUserRepository.existsByEmail("email@example.com")).thenReturn(true);
        boolean result = userRepository.existsByEmail("email@example.com");
        assertTrue(result);
    }

    @Test
    void testExistsByUsername() {
        when(jpaUserRepository.existsByUsername("username")).thenReturn(true);
        boolean result = userRepository.existsByUsername("username");
        assertTrue(result);
    }

    @Test
    void testSaveUserSuccess() {
        UserEntity entity = UserEntity.toUserEntity(user);
        when(jpaUserRepository.save(any(UserEntity.class))).thenReturn(entity);
        User result = userRepository.saveUser(user);
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void testSaveUserThrowsException() {
        when(jpaUserRepository.save(any(UserEntity.class))).thenThrow(new DataBaseException("DB error"));
        assertThrows(DataBaseException.class, () -> userRepository.saveUser(user));
    }

    @Test
    void testValidateUserSuccess() {
        String rawPassword = "password";
        UserEntity entity = UserEntity.toUserEntity(user);
        entity.setPassword("hashedPassword");

        when(jpaUserRepository.findUserByEmailAndPassword(user.getEmail(), rawPassword)).thenReturn(Optional.of(entity));
        when(passwordEncoder.matches(rawPassword, "hashedPassword")).thenReturn(true);

        Optional<User> result = userRepository.validateUser(user.getEmail(), rawPassword);
        assertTrue(result.isPresent());
    }

    @Test
    void testValidateUserWrongPassword() {
        String rawPassword = "wrong";
        UserEntity entity = UserEntity.toUserEntity(user);
        entity.setPassword("hashedPassword");

        when(jpaUserRepository.findUserByEmailAndPassword(user.getEmail(), rawPassword)).thenReturn(Optional.of(entity));
        when(passwordEncoder.matches(rawPassword, "hashedPassword")).thenReturn(false);

        Optional<User> result = userRepository.validateUser(user.getEmail(), rawPassword);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindAllUsers() {
        List<UserEntity> entities = List.of(UserEntity.toUserEntity(user));
        when(jpaUserRepository.findAll()).thenReturn(entities);

        List<User> users = userRepository.findAllUsers();
        assertEquals(1, users.size());
        assertEquals(user.getEmail(), users.get(0).getEmail());
    }

    @Test
    void testFindUserById() {
        UUID id = user.getId();
        when(jpaUserRepository.findById(id)).thenReturn(Optional.of(UserEntity.toUserEntity(user)));

        Optional<User> result = userRepository.findUserById(id);
        assertTrue(result.isPresent());
        assertEquals(user.getId(), result.get().getId());
    }

    @Test
    void testUpdateUser() {
        UserEntity entity = UserEntity.toUserEntity(user);
        when(jpaUserRepository.save(any(UserEntity.class))).thenReturn(entity);

        User updated = userRepository.updateUser(user);
        assertEquals(user.getId(), updated.getId());
    }

    @Test
    void testDeleteUserSuccess() {
        UserEntity entity = UserEntity.toUserEntity(user);
        UUID id = user.getId();

        when(jpaUserRepository.findById(id)).thenReturn(Optional.of(entity));
        doNothing().when(jpaUserRepository).deleteById(id);

        Optional<User> result = userRepository.deleteUser(id);
        assertTrue(result.isPresent());
        assertEquals(user.getId(), result.get().getId());
    }

    @Test
    void testDeleteUserNotFound() {
        UUID id = UUID.randomUUID();
        when(jpaUserRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(DataBaseException.class, () -> userRepository.deleteUser(id));
    }
}