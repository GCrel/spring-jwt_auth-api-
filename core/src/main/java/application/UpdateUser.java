package application;

import models.User;
import port.input.IUpdateUserInput;
import port.output.IUserRepository;

import java.util.UUID;

public class UpdateUser implements IUpdateUserInput {
    private final IUserRepository userRepository;
    public UpdateUser(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public User updateUser(UUID id, User newUser) {
        User existingUser = userRepository.findUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        if (newUser.getEmail() != null) {
            if (userRepository.existsByEmail(newUser.getEmail()) && !newUser.getEmail().equals(existingUser.getEmail())) {
                throw new IllegalArgumentException("Email already exists: " + newUser.getEmail());
            }
            existingUser.setEmail(newUser.getEmail());
        }
        if (newUser.getPassword() != null) {
            existingUser.setPassword(newUser.getPassword());
        }
        if (newUser.getUsername() != null) {
            if (userRepository.existsByUsername(newUser.getUsername()) && !newUser.getUsername().equals(existingUser.getUsername())) {
                throw new IllegalArgumentException("Username already exists: " + newUser.getUsername());
            }
            existingUser.setUsername(newUser.getUsername());
        }
        if (newUser.getRole() != null) {
            existingUser.setRole(newUser.getRole());
        }
        return userRepository.updateUser(existingUser);
    }
}
