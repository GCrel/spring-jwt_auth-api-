import application.RegisterUser;
import application.exceptions.UserAlreadyExistsExceptions;
import models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import port.input.IRegisterUserInput;
import port.output.IUserRepository;
import utils.UserFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RegisterUserTest {
    private IRegisterUserInput registerUserUseCase;
    @Mock
    private IUserRepository userRepository;

    @BeforeEach
    public void setUp() {
        registerUserUseCase = new RegisterUser(userRepository);
    }

    @Test
    public void registerUser_UsernameAndEmailNotExists_ShouldRegisterUser() {
        // Arrange
        User newUser = UserFactory.createUser();

        // Mock
        when(userRepository.existsByUsername(newUser.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(newUser.getEmail())).thenReturn(false);
        when(userRepository.saveUser(newUser)).thenReturn(newUser);
        // Act
        User registeredUser = registerUserUseCase.registerUser(newUser);

        // Assert
        assertNotNull(registeredUser);
        assertEquals(newUser.getUsername(), registeredUser.getUsername());
        assertEquals(newUser.getEmail(), registeredUser.getEmail());

        verify(userRepository).existsByUsername(newUser.getUsername());
        verify(userRepository).existsByEmail(newUser.getEmail());
        verify(userRepository).saveUser(newUser);
    }

    @Test
    public void registerUser_UsernameExists_ExpectsException() {
        // Arrange
        User newUser = UserFactory.createUser();

        // Mock
        when(userRepository.existsByUsername(newUser.getUsername())).thenReturn(true);

        // Act & Assert
        try {
            registerUserUseCase.registerUser(newUser);
        } catch (UserAlreadyExistsExceptions e) {
            assertEquals("Username already exists", e.getMessage());
        }

        verify(userRepository).existsByUsername(newUser.getUsername());
    }

    @Test
    public void registerUser_EmailExists_ExpectsException() {
        // Arrange
        User newUser = UserFactory.createUser();

        // Mock
        when(userRepository.existsByEmail(newUser.getEmail())).thenReturn(true);

        // Act & Assert
        try {
            registerUserUseCase.registerUser(newUser);
        } catch (UserAlreadyExistsExceptions e) {
            assertEquals("Email already exists", e.getMessage());
        }

        verify(userRepository).existsByEmail(newUser.getEmail());
    }
}
