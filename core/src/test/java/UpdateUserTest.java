import application.UpdateUser;
import models.User;
import models.vo.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import port.input.IUpdateUserInput;
import port.output.IUserRepository;
import utils.UserFactory;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UpdateUserTest {
    private IUpdateUserInput updateUserUseCase;
    @Mock
    private IUserRepository userRepository;

    @BeforeEach
    public void setUp() {
        updateUserUseCase = new UpdateUser(userRepository);
    }

    @Test
    public void updateUser_ValidFields_UpdatesUserSuccessfully() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User existingUser = UserFactory.createUserWithId(userId);
        User updatedData = UserFactory.createUser();
        updatedData.setEmail("new@mail.com");
        updatedData.setUsername("newUsername");
        updatedData.setPassword("newPassword");
        updatedData.setRole(Role.ADMIN);

        when(userRepository.findUserById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("new@mail.com")).thenReturn(false);
        when(userRepository.existsByUsername("newUsername")).thenReturn(false);
        when(userRepository.updateUser(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = updateUserUseCase.updateUser(userId, updatedData);

        // Assert
        assertEquals("new@mail.com", result.getEmail());
        assertEquals("newUsername", result.getUsername());
        assertEquals("newPassword", result.getPassword());
        assertEquals(Role.ADMIN, result.getRole());

        verify(userRepository).updateUser(existingUser);
    }

    @Test
    public void updateUser_UserNotFound_ThrowsException() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User updatedData = UserFactory.createUser();

        when(userRepository.findUserById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            updateUserUseCase.updateUser(userId, updatedData);
        });
        assertTrue(ex.getMessage().contains("User not found with id"));
    }

    @Test
    public void updateUser_EmailAlreadyExists_ThrowsException() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User existingUser = UserFactory.createUserWithId(userId);
        User updatedData = UserFactory.createUser();
        updatedData.setEmail("existing@mail.com");

        when(userRepository.findUserById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("existing@mail.com")).thenReturn(true);

        // Act & Assert
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            updateUserUseCase.updateUser(userId, updatedData);
        });
        assertTrue(ex.getMessage().contains("Email already exists"));
    }

    @Test
    public void updateUser_UsernameAlreadyExists_ThrowsException() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User existingUser = UserFactory.createUserWithId(userId);
        User updatedData = UserFactory.createUser();
        updatedData.setUsername("usedUsername");

        when(userRepository.findUserById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByUsername("usedUsername")).thenReturn(true);

        // Act & Assert
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            updateUserUseCase.updateUser(userId, updatedData);
        });
        assertTrue(ex.getMessage().contains("Username already exists"));
    }

    @Test
    public void updateUser_NoChanges_UpdatesNothing() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User existingUser = UserFactory.createUserWithId(userId);
        User updatedData = UserFactory.createUser(); // all fields null

        when(userRepository.findUserById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.updateUser(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = updateUserUseCase.updateUser(userId, updatedData);

        // Assert
        assertEquals(existingUser, result);
        verify(userRepository).updateUser(existingUser);
    }

}
