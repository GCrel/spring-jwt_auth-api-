import models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import port.input.IGetAllUsersInput;
import port.output.IUserRepository;
import utils.UserFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetAllUsersTest {
    private IGetAllUsersInput getAllUsersUseCase;
    @Mock
    private IUserRepository userRepository;

    @BeforeEach
    public void setUp() {
        getAllUsersUseCase = new application.GetAllUsers(userRepository);
    }

    @Test
    public void getAllUsers_ReturnsAllUsers() {
        // Arrange
        List<User> users = UserFactory.createListOfUsers(5);
        when(userRepository.findAllUsers()).thenReturn(users);

        // Act
        List<User> result = getAllUsersUseCase.getAllUsers();

        // Assert
        assertEquals(users.size(), result.size());
        assertEquals(users, result);
        verify(userRepository).findAllUsers();
    }

    @Test
    public void getAllUsers_ReturnsEmptyList() {
        // Arrange
        when(userRepository.findAllUsers()).thenReturn(List.of());

        // Act
        List<User> result = getAllUsersUseCase.getAllUsers();

        // Assert
        assertEquals(0, result.size());
        assertTrue(result.isEmpty());
        verify(userRepository).findAllUsers();
    }
}
