package utils;

import models.User;
import models.vo.Role;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UserFactory {
    public static User createUser() {
        return new User("testuser", "testEmail@mail.com", "password_123", Role.USER);
    }

    public static User createUserWithId(UUID userId) {
        User user = createUser();
        user.setId(userId);
        return user;
    }

    public static User createUser(Role role) {
        return new User("testuser", "testEmail@mail.com", "password_123", role);
    }

    public static List<User> createListOfUsers(int size) {
        return IntStream.range(0, size).mapToObj(i -> createUser()).collect(Collectors.toList());
    }
}
