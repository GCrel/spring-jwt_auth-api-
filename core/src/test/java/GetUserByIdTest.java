import application.GetUserById;
import application.exceptions.UserNotFoundException;
import models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import port.input.IGetUserByIdInput;
import port.output.IUserRepository;
import utils.UserFactory;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetUserByIdTest {
    private IGetUserByIdInput getUserByIdUseCase;
    @Mock
    private IUserRepository userRepository;

    @BeforeEach
    public void setUp() {
        getUserByIdUseCase = new GetUserById(userRepository);
    }

    @Test
    public void getUserById_ValidId_ReturnsUser() {
        // Arrange
        User user = UserFactory.createUser();
        when(userRepository.findUserById(user.getId())).thenReturn(Optional.of(user));

        // Act
        User result = getUserByIdUseCase.getUserById(user.getId());

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(user.getId(), result.getId());
        assertEquals(user, result);
    }

    @Test
    public void getUserById_InvalidId_ThrowsUserNotFoundException() {
        // Arrange
        User user = UserFactory.createUser();
        when(userRepository.findUserById(user.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class,
                () -> getUserByIdUseCase.getUserById(user.getId()),
                "User not found with id: " + user.getId()
        );
    }
}
