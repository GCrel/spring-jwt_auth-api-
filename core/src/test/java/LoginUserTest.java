import application.LoginUser;
import application.exceptions.InvalidCredentialsException;
import application.exceptions.UserNotFoundException;
import models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import port.input.ILoginUserInput;
import port.output.IUserRepository;
import utils.UserFactory;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoginUserTest {
    private ILoginUserInput loginUserUseCase;
    @Mock
    private IUserRepository userRepository;

    @BeforeEach
    public void setUp() {
        loginUserUseCase = new LoginUser(userRepository);
    }

    @Test
    public void loginUser_UserNotFound_ThrowsUserNotFoundException() {
        // Arrange
        String email = "mailInexistente@mail.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            loginUserUseCase.loginUser(email, "password");
        });

        verify(userRepository, never()).validateUser(anyString(), anyString());
    }

    @Test
    public void loginUser_UserExistsButInvalidCredentials_ThrowsInvalidCredentialsException() {
        // Arrange
        String email = "existingUser@mail.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);
        when(userRepository.validateUser(email, "password")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvalidCredentialsException.class,
                () -> {
                    loginUserUseCase.loginUser(email, "password");
                });
    }

    @Test
    public void loginUser_UserExistsAndValidCredentials_ReturnsUser() {
        // Arrange
        User user = UserFactory.createUser();
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);
        when(userRepository.validateUser(user.getEmail(), user.getPassword())).thenReturn(Optional.of(user));

        // Act
        User loggedInUser = loginUserUseCase.loginUser(user.getEmail(), user.getPassword());

        // Assert
        assertNotNull(loggedInUser);
        assertEquals(user, loggedInUser); // Si User implementa equals correctamente

        verify(userRepository).existsByEmail(user.getEmail());
        verify(userRepository).validateUser(user.getEmail(), user.getPassword());
    }
}
