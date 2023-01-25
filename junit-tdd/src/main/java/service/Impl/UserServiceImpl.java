package service.Impl;

import data.UsersRepository;
import data.UsersRepositoryImpl;
import exception.UserServiceException;
import model.User;
import service.EmailVerificationService;
import service.UserService;

import java.util.UUID;

public class UserServiceImpl implements UserService {

    UsersRepository usersRepository;
    EmailVerificationService emailVerificationService;

    public UserServiceImpl(UsersRepository usersRepository, EmailVerificationService emailVerificationService) {
        this.usersRepository = usersRepository;
        this.emailVerificationService = emailVerificationService;
    }

    @Override
    public User createUser(String firstName, String lastName, String email, String password, String repeatPassword) {

        if(firstName == null || firstName.trim().length() == 0) {
            throw new IllegalArgumentException("User's first name is empty");
        }
        User user = new User(firstName, lastName, email, UUID.randomUUID().toString());
        boolean isUserCreated = false;
        try {
            isUserCreated = usersRepository.save(user);
        } catch (RuntimeException e) {
            throw new UserServiceException(e.getMessage());
        }

        if(!isUserCreated) throw new UserServiceException("Could not create user");

        try {
            emailVerificationService.scheduleEmailConfirmation(user);
        } catch (RuntimeException ex) {
            throw new UserServiceException(ex.getMessage());
        }
        return user;
    }
}
