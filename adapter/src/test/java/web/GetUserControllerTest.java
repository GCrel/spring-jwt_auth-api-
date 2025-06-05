package web;

import application.exceptions.UserNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.GCrel.Main;
import com.github.GCrel.web.controller.GetUserController;
import com.github.GCrel.web.services.JWTService;
import config.TestSecurityConfig;
import models.User;
import models.vo.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import port.input.IGetAllUsersInput;
import port.input.IGetUserByIdInput;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GetUserController.class)
@ContextConfiguration(classes = Main.class)
@Import(TestSecurityConfig.class)
public class GetUserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IGetAllUsersInput getAllUsersUseCase;

    @MockitoBean
    private IGetUserByIdInput getUserByIdUseCase;

    @MockitoBean
    private JWTService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnListOfUsersWhenUsersExist() throws Exception {
        List<User> users = List.of(
                new User("User1", "user1@example.com", "password", Role.USER),
                new User("User2", "user2@example.com", "password", Role.ADMIN)
        );
        when(getAllUsersUseCase.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }
    @Test
    void shouldReturnNoContentWhenNoUsersExist() throws Exception {
        when(getAllUsersUseCase.getAllUsers()).thenReturn(List.of());

        mockMvc.perform(get("/users"))
                .andExpect(status().isNoContent());
    }
    @Test
    void shouldReturnUserWhenAdminRequestsById() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = new User("adminUser", "admin@example.com", "password", Role.ADMIN);
        user.setId(userId);

        when(jwtService.extractTokenFromRequest(any())).thenReturn("dummy-token");
        when(jwtService.extractRole("dummy-token")).thenReturn("ADMIN");
        when(jwtService.extractId("dummy-token")).thenReturn(UUID.randomUUID().toString());
        when(getUserByIdUseCase.getUserById(userId)).thenReturn(user);

        mockMvc.perform(get("/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@example.com"));
    }

    @Test
    void shouldAllowUserAccessToOwnData() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = new User("regularUser", "user@example.com", "password", Role.USER);
        user.setId(userId);

        when(jwtService.extractTokenFromRequest(any())).thenReturn("dummy-token");
        when(jwtService.extractRole("dummy-token")).thenReturn("USER");
        when(jwtService.extractId("dummy-token")).thenReturn(userId.toString());
        when(getUserByIdUseCase.getUserById(userId)).thenReturn(user);

        mockMvc.perform(get("/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }

    @Test
    void shouldReturnForbiddenWhenUserTriesToAccessAnotherUser() throws Exception {
        UUID targetUserId = UUID.randomUUID();
        UUID loggedUserId = UUID.randomUUID();

        User user = new User("OtherUser", "other@example.com", "pass", Role.USER);

        when(jwtService.extractTokenFromRequest(any())).thenReturn("dummy-token");
        when(jwtService.extractRole("dummy-token")).thenReturn("USER");
        when(jwtService.extractId("dummy-token")).thenReturn(loggedUserId.toString());

        when(getUserByIdUseCase.getUserById(targetUserId)).thenReturn(user);

        mockMvc.perform(get("/users/" + targetUserId))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        UUID userId = UUID.randomUUID();

        when(jwtService.extractTokenFromRequest(any())).thenReturn("dummy-token");
        when(jwtService.extractRole("dummy-token")).thenReturn("ADMIN");
        when(jwtService.extractId("dummy-token")).thenReturn("some-id");
        when(getUserByIdUseCase.getUserById(userId)).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(get("/users/" + userId))
                .andExpect(status().isNotFound());
    }
}
