package port.input;

import models.User;

public interface ILoginUserInput {
    User loginUser(String email, String password);
}
