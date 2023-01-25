package service;

import data.UsersRepository;
import exception.EmailNotificationServiceException;
import exception.UserServiceException;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import service.Impl.UserServiceImpl;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    UserServiceImpl userService; //Since this service has a dependency, we use InjectMocks so his dependencies are automatically injected by Mockito. In this case, UsersRepository
    @Mock
    UsersRepository usersRepository;
    @Mock
    EmailVerificationService emailVerificationService;
    String firstName;
    String lastName;
    String email;
    String password;
    String repeatPassword;

    @BeforeEach
    void init() {
        firstName = "Bruno";
        lastName = "Affeldt";
        email = "email@email.com";
        password = "1234560";
        repeatPassword = "1234560";
    }

    @DisplayName("User Object Created")
    @Test
    void testCreateUser_whenUserDetailsProvided_returnsUserObject() {
        //Arrange
        Mockito.when(usersRepository.save(any(User.class))).thenReturn(true);

        //Act
        User user = userService.createUser(firstName, lastName, email, password, repeatPassword);

        //Assert
        assertNotNull(user, "The createUser() method should not return null");
        assertEquals(firstName, user.getFirstName(), "User's first name is incorrect");
        assertEquals(lastName, user.getLastName(), "User's last name is incorrect");
        assertEquals(email, user.getEmail(), "User's email is incorrect");
        assertNotNull(user.getId(), "id should not be null");
        verify(usersRepository).save(any(User.class));
    }

    @DisplayName("Empty first name causes correct exception")
    @Test
    void testCreateUser_whenFirstNameIsEmpty_throwsIllegalArgumentException() {
        //Arrange
        String firstName = "";
        String expectedExceptionMessage = "User's first name is empty";

        //Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(firstName, lastName, email, password, repeatPassword);
        }, "Empty first name should have caused an Illegal Argument Exception");

        assertEquals(expectedExceptionMessage, exception.getMessage(), "Exception error message is not correct");

    }

    @DisplayName("If save() method causes RuntimeException, a UserServiceException is thrown")
    @Test
    void testCreateUser_whenSaveMethodThrowsException_thenThrowsUserServiceException() {
        //Arrange
        when(usersRepository.save(any(User.class))).thenThrow(RuntimeException.class);

        //Act & Assert
        assertThrows(UserServiceException.class, () -> userService.createUser(firstName, lastName, email, password, repeatPassword), "Should have thrown UserServiceException instead");
    }

    @Test
    void testCreateUser_whenEmailNotificationExceptionThrown_thriwsUserServiceException() {
        //Arrange
        when(usersRepository.save(any(User.class))).thenReturn(true);
        doThrow(EmailNotificationServiceException.class).when(emailVerificationService).scheduleEmailConfirmation(any(User.class));
        // doNothing().when(emailVerificationService).scheduleEmailConfirmation(any(User.class));  --> this is used to do nothing when void method

        //Act
        assertThrows(UserServiceException.class, () -> {
            userService.createUser(firstName, lastName, email, password, repeatPassword);
        }, "Should have thrown UserServiceException instead");

        //Assert
        verify(emailVerificationService, times(1)).scheduleEmailConfirmation(any(User.class));
    }

    @DisplayName("Schedule email confirmation is executed")
    @Test
    void testCreateUser_whenUserCreated_scheduleEmailConfirmation() {
        when(usersRepository.save(any(User.class))).thenReturn(true);
        doCallRealMethod().when(emailVerificationService).scheduleEmailConfirmation(any(User.class));

        userService.createUser(firstName, lastName, email, password, repeatPassword);
        verify(emailVerificationService, times(1)).scheduleEmailConfirmation(any(User.class));
    }

}
