package application;

import models.User;
import port.input.IGetAllUsersInput;
import port.output.IUserRepository;

import java.util.List;

public class GetAllUsers implements IGetAllUsersInput {
    private final IUserRepository userRepository;
    public GetAllUsers(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public List<User> getAllUsers() {
       return userRepository.findAllUsers();
    }
}
