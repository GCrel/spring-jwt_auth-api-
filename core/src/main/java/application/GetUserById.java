package application;

import application.exceptions.UserNotFoundException;
import models.User;
import port.input.IGetUserByIdInput;
import port.output.IUserRepository;

import java.util.UUID;

public class GetUserById implements IGetUserByIdInput {
    private final IUserRepository userRepository;
    public GetUserById(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public User getUserById(UUID id) {
        return userRepository.findUserById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }
}
