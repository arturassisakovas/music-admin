
package com.mAdmin.service;

import com.mAdmin.entity.PasswordResetToken;
import com.mAdmin.entity.User;


public interface PasswordResetTokenService {

    PasswordResetToken formAndSavePasswordResetToken(User user);

}
