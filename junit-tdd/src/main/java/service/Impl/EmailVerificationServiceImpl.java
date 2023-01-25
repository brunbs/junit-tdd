package service.Impl;

import model.User;
import service.EmailVerificationService;

public class EmailVerificationServiceImpl implements EmailVerificationService {
    @Override
    public void scheduleEmailConfirmation(User user) {
        //Put user details into email queue
    }
}
