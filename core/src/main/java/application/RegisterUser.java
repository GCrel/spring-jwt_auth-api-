package application;

import application.exceptions.UserAlreadyExistsExceptions;
import models.User;
import port.input.IRegisterUserInput;
import port.output.IUserRepository;

public class RegisterUser implements IRegisterUserInput {
    private final IUserRepository userRepository;
    public RegisterUser(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User registerUser(User newUser) {
        if (userRepository.existsByUsername(newUser.getUsername())) {
            throw new UserAlreadyExistsExceptions("Username already exists");
        }
        if (userRepository.existsByEmail(newUser.getEmail())) {
            throw new UserAlreadyExistsExceptions("Email already exists");
        }
        // Assign a new UUID to the user
        User user = new User(
                newUser.getUsername(),
                newUser.getEmail(),
                newUser.getPassword(),
                newUser.getRole()
        );
        return userRepository.saveUser(user);
    }
}
