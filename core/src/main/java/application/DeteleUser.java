package application;

import application.exceptions.UserNotFoundException;
import models.User;
import port.input.IDeleteUserInput;
import port.output.IUserRepository;

import java.util.UUID;

public class DeteleUser implements IDeleteUserInput {
    private final IUserRepository userRepository;
    public DeteleUser(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User deleteUser(UUID id) {
        return userRepository.deleteUser(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }
}
