package service.Impl;

import model.User;
import service.UserService;

import java.util.UUID;

public class UserServiceImpl implements UserService {
    @Override
    public User createUser(String firstName, String lastName, String email, String password, String repeatPassword) {

        if(firstName == null || firstName.trim().length() == 0) {
            throw new IllegalArgumentException("User's first name is empty");
        }

        return new User(firstName, lastName, email, UUID.randomUUID().toString());
    }
}
