package web;

import application.exceptions.UserAlreadyExistsExceptions;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.GCrel.Main;
import com.github.GCrel.web.controller.AuthController;
import com.github.GCrel.web.controller.vo.LoginRequest;
import com.github.GCrel.web.models.UserDTO;
import com.github.GCrel.web.services.JWTService;
import config.TestSecurityConfig;
import models.User;
import models.vo.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import port.input.ILoginUserInput;
import port.input.IRegisterUserInput;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@ContextConfiguration(classes = Main.class)
@Import(TestSecurityConfig.class)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ILoginUserInput loginUserUseCase;

    @MockitoBean
    private IRegisterUserInput registerUserUseCase;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private JWTService jwtService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        UserDTO userDTO = UserDTO.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .role(Role.USER)
                .build();

        UserDTO registeredUser = UserDTO.builder()
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .role(Role.USER)
                .build();

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(registerUserUseCase.registerUser(any(User.class))).thenReturn(registeredUser.toUser());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void shouldReturnBadRequestWhenRegisteringExistingUser() throws Exception {
        UserDTO userDTO = UserDTO.builder()
                .username("existinguser")
                .email("exist@example.com")
                .password("password123")
                .role(Role.USER)
                .build();

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(registerUserUseCase.registerUser(any(User.class))).thenThrow(new UserAlreadyExistsExceptions("User exists"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldLoginSuccessfullyAndReturnToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest("login@example.com", "password123");

        UserDTO userDTO = UserDTO.builder()
                .username("loginuser")
                .email("login@example.com")
                .password("encodedPassword")
                .role(Role.USER)
                .build();

        when(loginUserUseCase.loginUser("login@example.com", "password123")).thenReturn(userDTO.toUser());
        when(jwtService.generateToken(any(User.class))).thenReturn("mocked-jwt-token");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("mocked-jwt-token"));
    }

    @Test
    void shouldReturnBadRequestOnLoginFailure() throws Exception {
        LoginRequest loginRequest = new LoginRequest("fail@example.com", "wrongpassword");

        when(loginUserUseCase.loginUser(anyString(), anyString()))
                .thenThrow(new UserAlreadyExistsExceptions("Invalid credentials"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

}
