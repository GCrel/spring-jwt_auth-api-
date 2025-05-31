package port.output;

import models.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IUserRepository {
    /* * checks if a user with the given email exists in the repository.
     * @param email The email to check.
     * @return true if a user with the given email exists, false otherwise.
     */
    boolean existsByEmail(String email);

    /* * Checks if a user with the given username exists in the repository.
     * @param username The username to check.
     * @return true if a user with the given username exists, false otherwise.
     */
    boolean existsByUsername(String username);

    /* * Saves a user to the repository.
     * @param user The user to save.
     * @return The saved user.
     */
    User saveUser(User user);

    /* * Validates a user by checking if the email and password match an existing user.
     * @param email The email of the user to validate.
     * @param password The password of the user to validate.
     * @return An Optional containing the User if validation is successful, or an empty Optional if not.
     */
    Optional<User> validateUser(String email, String password);

    /* * Returns all users in the repository.
     * @param email The email of the user to find.
     * @return An Optional containing the User if found, or an empty Optional if not.
     */
    List<User> findAllUsers();

    /* * Finds a user by their ID.
     * @param id The ID of the user to find.
     * @return An Optional containing the User if found, or an empty Optional if not.
     */
    Optional<User> findUserById(UUID id);

    /* * Updates a user in the repository.
     * @param user The user to update.
     * @return The updated user.
     */
    User updateUser(User user);

    /* * Deletes a user by their ID.
     * @param id The ID of the user to delete.
     * @return The deleted user, or null if no user was found with the given ID.
     */
    Optional<User> deleteUser(UUID id);
}
