import application.DeteleUser;
import application.exceptions.UserNotFoundException;
import models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import port.input.IDeleteUserInput;
import port.output.IUserRepository;
import utils.UserFactory;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DeleteUserTest {
    private IDeleteUserInput deleteUserUseCase;
    @Mock
    private IUserRepository userRepository;

    @BeforeEach
    public void setUp() {
        deleteUserUseCase = new DeteleUser(userRepository);
    }

    @Test
    public void deleteUser_UserExists_ShouldDeleteUser() {
        // Arrange
        User user = UserFactory.createUser();
        when(userRepository.deleteUser(user.getId())).thenReturn(Optional.of(user));

        // Act
        User deletedUser = deleteUserUseCase.deleteUser(user.getId());

        // Assert
        assertNotNull(deletedUser);
        assertEquals(user, deletedUser);
        verify(userRepository).deleteUser(user.getId());
    }

    @Test
    public void deleteUser_UserDoesNotExist_ThrowsUserNotFoundException() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(userRepository.deleteUser(id)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            deleteUserUseCase.deleteUser(id);
        });
        assertTrue(exception.getMessage().contains("User not found with id"));
        verify(userRepository).deleteUser(id);
    }
}
