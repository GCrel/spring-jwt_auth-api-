package port.input;

import models.User;

import java.util.UUID;

public interface IUpdateUserInput {
    User updateUser(UUID id, User newUser);
}
