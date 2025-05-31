package port.input;

import models.User;

import java.util.UUID;

public interface IDeleteUserInput {
    User deleteUser(UUID id);
}
