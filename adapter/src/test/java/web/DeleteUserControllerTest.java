package web;

import application.exceptions.UserNotFoundException;
import com.github.GCrel.Main;
import com.github.GCrel.web.controller.DeleteUserController;
import com.github.GCrel.web.services.JWTService;
import config.TestSecurityConfig;
import models.User;
import models.vo.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import port.input.IDeleteUserInput;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DeleteUserController.class)
@ContextConfiguration(classes = Main.class)
@Import(TestSecurityConfig.class)
class DeleteUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IDeleteUserInput deleteUserUseCase;
    @MockitoBean
    private JWTService jwtService;

    private final UUID userId = UUID.fromString("44776b53-6de1-4880-995c-b6ac5aa05281");

    @Test
    void deleteUserById_ShouldReturnOkWithUser() throws Exception {
        UUID userId = UUID.randomUUID();

        User user = new User("Test User", "test@example.com", "adminPass", Role.ADMIN);
        user.setId(userId);

        when(deleteUserUseCase.deleteUser(userId)).thenReturn(user);

        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void deleteUserById_WhenUserNotFound_ShouldReturnForbidden() throws Exception {
        UUID userId = UUID.randomUUID();

        when(deleteUserUseCase.deleteUser(userId))
                .thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isForbidden());
    }
}
