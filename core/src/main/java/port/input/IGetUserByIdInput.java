package port.input;

import models.User;

import java.util.UUID;

public interface IGetUserByIdInput {
    User getUserById(UUID id);
}
