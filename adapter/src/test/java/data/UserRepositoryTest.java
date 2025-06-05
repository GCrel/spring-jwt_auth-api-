package data;

import com.github.GCrel.data.jpa.IJPAUserRepository;
import com.github.GCrel.data.models.UserEntity;
import com.github.GCrel.data.services.UserRepository;
import com.github.GCrel.data.services.exception.DataBaseException;
import models.User;
import models.vo.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {
    @Mock
    private IJPAUserRepository jpaUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserRepository userRepository;

    private User testUser;
    private UserEntity testUserEntity;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUser = new User("testuser", "test@example.com", "password123",Role.USER);
        testUser.setId(testUserId);
        testUserEntity = UserEntity.toUserEntity(testUser);
    }

    @Test
    void existsByEmail_ShouldReturnTrue_WhenEmailExists() {
        // Given
        String email = "test@example.com";
        when(jpaUserRepository.existsByEmail(email)).thenReturn(true);

        // When
        boolean result = userRepository.existsByEmail(email);

        // Then
        assertTrue(result);
        verify(jpaUserRepository).existsByEmail(email);
    }

    @Test
    void existsByEmail_ShouldReturnFalse_WhenEmailDoesNotExist() {
        // Given
        String email = "nonexistent@example.com";
        when(jpaUserRepository.existsByEmail(email)).thenReturn(false);

        // When
        boolean result = userRepository.existsByEmail(email);

        // Then
        assertFalse(result);
        verify(jpaUserRepository).existsByEmail(email);
    }

    @Test
    void existsByUsername_ShouldReturnTrue_WhenUsernameExists() {
        // Given
        String username = "testuser";
        when(jpaUserRepository.existsByUsername(username)).thenReturn(true);

        // When
        boolean result = userRepository.existsByUsername(username);

        // Then
        assertTrue(result);
        verify(jpaUserRepository).existsByUsername(username);
    }

    @Test
    void existsByUsername_ShouldReturnFalse_WhenUsernameDoesNotExist() {
        // Given
        String username = "nonexistentuser";
        when(jpaUserRepository.existsByUsername(username)).thenReturn(false);

        // When
        boolean result = userRepository.existsByUsername(username);

        // Then
        assertFalse(result);
        verify(jpaUserRepository).existsByUsername(username);
    }

    @Test
    void saveUser_ShouldReturnSavedUser_WhenSuccessful() {
        // Given
        UserEntity savedUserEntity = UserEntity.builder()
                .id(testUserId)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .role(Role.USER)
                .build();
        when(jpaUserRepository.save(any(UserEntity.class))).thenReturn(savedUserEntity);

        // When
        User result = userRepository.saveUser(testUser);

        // Then
        assertNotNull(result);
        assertEquals(testUserId, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(jpaUserRepository).save(any(UserEntity.class));
    }

    @Test
    void saveUser_ShouldThrowDataBaseException_WhenSaveFails() {
        // Given
        when(jpaUserRepository.save(any(UserEntity.class)))
                .thenThrow(new DataBaseException("Database error"));

        // When & Then
        DataBaseException exception = assertThrows(DataBaseException.class,
                () -> userRepository.saveUser(testUser));

        assertTrue(exception.getMessage().contains("Could not save user"));
        verify(jpaUserRepository).save(any(UserEntity.class));
    }

    @Test
    void validateUser_ShouldReturnUser_WhenCredentialsAreValid() {
        // Given
        String email = "test@example.com";
        String password = "password123";

        // No necesitas doNothing() aquí
        when(jpaUserRepository.findByEmail(email)).thenReturn(Optional.of(testUserEntity));

        // When
        Optional<User> result = userRepository.validateUser(email, password);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser.getEmail(), result.get().getEmail());
        assertEquals(testUser.getUsername(), result.get().getUsername());

        verify(authenticationManager).authenticate(
                argThat(token -> token.getPrincipal().equals(email) &&
                        token.getCredentials().equals(password))
        );
        verify(jpaUserRepository).findByEmail(email);
    }

    @Test
    void validateUser_ShouldThrowException_WhenCredentialsAreInvalid() {
        // Given
        String email = "test@example.com";
        String password = "wrongpassword";

        doThrow(new BadCredentialsException("Invalid credentials"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        // When & Then
        assertThrows(BadCredentialsException.class,
                () -> userRepository.validateUser(email, password));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jpaUserRepository, never()).findByEmail(anyString());
    }

    @Test
    void validateUser_ShouldReturnEmpty_WhenUserNotFoundAfterAuthentication() {
        // Given
        String email = "test@example.com";
        String password = "password123";

        doAnswer(invocation -> null)
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        when(jpaUserRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userRepository.validateUser(email, password);

        // Then
        assertFalse(result.isPresent());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jpaUserRepository).findByEmail(email);
    }

    @Test
    void findAllUsers_ShouldReturnListOfUsers_WhenUsersExist() {
        // Given
        UserEntity user1 = UserEntity.builder()
                .id(UUID.randomUUID())
                .username("user1")
                .email("user1@example.com")
                .password("pass1")
                .role(Role.USER)
                .build();
        UserEntity user2 = UserEntity.builder()
                .id(UUID.randomUUID())
                .username("user2")
                .email("user2@example.com")
                .password("pass2")
                .role(Role.USER)
                .build();
        List<UserEntity> userEntities = Arrays.asList(user1, user2);

        when(jpaUserRepository.findAll()).thenReturn(userEntities);

        // When
        List<User> result = userRepository.findAllUsers();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getUsername());
        assertEquals("user2", result.get(1).getUsername());
        verify(jpaUserRepository).findAll();
    }

    @Test
    void findAllUsers_ShouldReturnEmptyList_WhenNoUsersExist() {
        // Given
        when(jpaUserRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<User> result = userRepository.findAllUsers();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(jpaUserRepository).findAll();
    }

    @Test
    void findUserById_ShouldReturnUser_WhenUserExists() {
        // Given
        when(jpaUserRepository.findById(testUserId)).thenReturn(Optional.of(testUserEntity));

        // When
        Optional<User> result = userRepository.findUserById(testUserId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUserId, result.get().getId());
        assertEquals("testuser", result.get().getUsername());
        verify(jpaUserRepository).findById(testUserId);
    }

    @Test
    void findUserById_ShouldReturnEmpty_WhenUserDoesNotExist() {
        // Given
        when(jpaUserRepository.findById(testUserId)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userRepository.findUserById(testUserId);

        // Then
        assertFalse(result.isPresent());
        verify(jpaUserRepository).findById(testUserId);
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser_WhenSuccessful() {
        // Given
        User updatedUser = new User("updateduser", "updated@example.com", "newpassword", Role.USER);
        updatedUser.setId(testUserId);
        UserEntity updatedUserEntity = UserEntity.toUserEntity(updatedUser);

        when(jpaUserRepository.save(any(UserEntity.class))).thenReturn(updatedUserEntity);

        // When
        User result = userRepository.updateUser(updatedUser);

        // Then
        assertNotNull(result);
        assertEquals(testUserId, result.getId());
        assertEquals("updateduser", result.getUsername());
        assertEquals("updated@example.com", result.getEmail());
        verify(jpaUserRepository).save(any(UserEntity.class));
    }

    @Test
    void deleteUser_ShouldReturnDeletedUser_WhenUserExists() {
        // Given
        when(jpaUserRepository.findById(testUserId)).thenReturn(Optional.of(testUserEntity));
        doNothing().when(jpaUserRepository).deleteById(testUserId);

        // When
        Optional<User> result = userRepository.deleteUser(testUserId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUserId, result.get().getId());
        assertEquals("testuser", result.get().getUsername());
        verify(jpaUserRepository).findById(testUserId);
        verify(jpaUserRepository).deleteById(testUserId);
    }

    @Test
    void deleteUser_ShouldThrowDataBaseException_WhenUserDoesNotExist() {
        // Given
        when(jpaUserRepository.findById(testUserId)).thenReturn(Optional.empty());

        // When & Then
        DataBaseException exception = assertThrows(DataBaseException.class,
                () -> userRepository.deleteUser(testUserId));

        assertTrue(exception.getMessage().contains("Task with ID " + testUserId + " not found"));
        verify(jpaUserRepository).findById(testUserId);
        verify(jpaUserRepository, never()).deleteById(any());
    }

    @Test
    void constructor_ShouldInjectDependencies() {
        // When & Then
        assertNotNull(userRepository);
    }
}