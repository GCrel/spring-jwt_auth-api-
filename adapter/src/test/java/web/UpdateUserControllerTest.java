package web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.GCrel.Main;
import com.github.GCrel.web.controller.UpdateUserController;
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
import port.input.IUpdateUserInput;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UpdateUserController.class)
@ContextConfiguration(classes = Main.class)
@Import(TestSecurityConfig.class)
public class UpdateUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IUpdateUserInput updateUserUseCase;

    @MockitoBean
    private JWTService jwtService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDTO createUserDTO(String password, Role role) {
        return UserDTO.builder()
                .id(UUID.randomUUID())
                .password(password)
                .role(role)
                .email("test@example.com")
                .username("Test User")
                .build();
    }

    @Test
    void adminUpdatesOtherUser_ShouldReturnOk() throws Exception {
        UUID targetUserId = UUID.randomUUID();
        UUID adminUserId = UUID.randomUUID();

        UserDTO requestDTO = createUserDTO("newPass", Role.USER);
        requestDTO.setId(targetUserId);
        User updatedUser = new User("Test User", "test@example.com", "encodedPass", Role.USER);
        updatedUser.setId(targetUserId);

        when(jwtService.extractTokenFromRequest(any())).thenReturn("dummy-token");
        when(jwtService.extractRole("dummy-token")).thenReturn("ADMIN");
        when(jwtService.extractId("dummy-token")).thenReturn(adminUserId.toString());
        when(passwordEncoder.encode("newPass")).thenReturn("encodedPass");

        when(updateUserUseCase.updateUser(eq(targetUserId), any(User.class)))
                .thenReturn(updatedUser);

        mockMvc.perform(put("/users/" + targetUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("")); // Aquí podrías validar también el contenido de la respuesta si esperas algo
    }

    @Test
    void adminUpdatesSelf_ShouldReturnUpdateRequest() throws Exception {
        UUID adminUserId = UUID.randomUUID();

        UserDTO requestDTO = createUserDTO("adminPass", Role.ADMIN);
        requestDTO.setId(adminUserId);
        User updatedUser = requestDTO.toUser();
        updatedUser.setId(adminUserId);

        when(jwtService.extractTokenFromRequest(any())).thenReturn("dummy-token");
        when(jwtService.extractRole("dummy-token")).thenReturn("ADMIN");
        when(jwtService.extractId("dummy-token")).thenReturn(adminUserId.toString());
        when(passwordEncoder.encode("adminPass")).thenReturn("encodedPass");
        when(updateUserUseCase.updateUser(eq(adminUserId), any(User.class))).thenReturn(updatedUser);
        when(jwtService.generateToken(any())).thenAnswer(invocation -> "user-new-token");

        mockMvc.perform(put("/users/" + adminUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.id").value(adminUserId.toString()))
                .andExpect(jsonPath("$.jwtToken").value("user-new-token"));
    }

    @Test
    void userUpdatesSelf_ShouldReturnUpdateRequestWithoutRoleChange() throws Exception {
        UUID userId = UUID.randomUUID();

        UserDTO requestDTO = createUserDTO("userPass", Role.ADMIN); // intenta cambiar rol
        requestDTO.setId(userId);
        User updatedUser = requestDTO.toUser();
        updatedUser.setId(userId);

        when(jwtService.extractTokenFromRequest(any())).thenReturn("user-token");
        when(jwtService.extractRole("user-token")).thenReturn("USER");
        when(jwtService.extractId("user-token")).thenReturn(userId.toString());
        when(passwordEncoder.encode("userPass")).thenReturn("encodedPass");
        when(updateUserUseCase.updateUser(eq(userId), any(User.class))).thenReturn(updatedUser);
        when(jwtService.generateToken(any())).thenReturn("user-new-token");

        mockMvc.perform(put("/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.id").value(userId.toString()))
                .andExpect(jsonPath("$.jwtToken").value("user-new-token"));
    }

    @Test
    void userUpdatesAnotherUser_ShouldReturnForbidden() throws Exception {
        UUID targetId = UUID.randomUUID();
        UUID userId = UUID.randomUUID(); // no es igual

        UserDTO requestDTO = createUserDTO("password", Role.USER);
        requestDTO.setId(targetId);

        when(jwtService.extractTokenFromRequest(any())).thenReturn("user-token");
        when(jwtService.extractRole("user-token")).thenReturn("USER");
        when(jwtService.extractId("user-token")).thenReturn(userId.toString());

        mockMvc.perform(put("/users/" + targetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void unknownRole_ShouldReturnForbidden() throws Exception {
        UUID id = UUID.randomUUID();

        UserDTO requestDTO = createUserDTO("pass", Role.USER);
        requestDTO.setId(id);

        when(jwtService.extractTokenFromRequest(any())).thenReturn("token");
        when(jwtService.extractRole("token")).thenReturn("UNKNOWN");
        when(jwtService.extractId("token")).thenReturn(id.toString());

        mockMvc.perform(put("/users/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateUser_ThrowsException_ShouldReturnInternalServerError() throws Exception {
        UUID id = UUID.randomUUID();

        UserDTO requestDTO = createUserDTO("fail", Role.USER);
        requestDTO.setId(id);

        when(jwtService.extractTokenFromRequest(any())).thenReturn("token");
        when(jwtService.extractRole("token")).thenReturn("ADMIN");
        when(jwtService.extractId("token")).thenReturn(id.toString());
        when(passwordEncoder.encode("fail")).thenThrow(new RuntimeException("fail"));

        mockMvc.perform(put("/users/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isInternalServerError());
    }
}
