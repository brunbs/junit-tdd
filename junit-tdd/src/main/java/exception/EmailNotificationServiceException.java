package exception;

import service.EmailVerificationService;

public class EmailNotificationServiceException extends RuntimeException{

    public EmailNotificationServiceException(String message) {
        super(message);
    }

}
