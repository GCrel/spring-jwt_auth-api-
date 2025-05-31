package application;

import application.exceptions.InvalidCredentialsException;
import application.exceptions.UserNotFoundException;
import models.User;
import port.input.ILoginUserInput;
import port.output.IUserRepository;

import java.util.Optional;

public class LoginUser implements ILoginUserInput {
    private final IUserRepository userRepository;
    public LoginUser(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public User loginUser(String email, String password) {
        if (!userRepository.existsByEmail(email)) {
            throw new UserNotFoundException("User with email " + email + " not found.");
        }
        return userRepository.validateUser(email, password)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials for user with email " + email + "."));
    }
}
