
package com.mAdmin.service;

import java.util.UUID;

import com.mAdmin.repository.ClientPasswordResetTokenRepository;
import com.mAdmin.repository.EmployeePasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mAdmin.entity.Client;
import com.mAdmin.entity.ClientPasswordResetToken;
import com.mAdmin.entity.Employee;
import com.mAdmin.entity.EmployeePasswordResetToken;
import com.mAdmin.entity.PasswordResetToken;
import com.mAdmin.entity.User;

@Service
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {

    @Autowired
    private ClientPasswordResetTokenRepository clientTokenRepository;

    @Autowired
    private EmployeePasswordResetTokenRepository employeeTokenRepository;

    private static final int expireDate = 30;

    @Override
    public PasswordResetToken formAndSavePasswordResetToken(User user) {

        if (user instanceof Client) {
            PasswordResetToken clientToken = formPasswordResetToken(new ClientPasswordResetToken(), user);
            clientTokenRepository.save((ClientPasswordResetToken) clientToken);

            return clientToken;
        } else if (user instanceof Employee) {
            PasswordResetToken employeeToken = formPasswordResetToken(new EmployeePasswordResetToken(), user);
            employeeTokenRepository.save((EmployeePasswordResetToken) employeeToken);

            return employeeToken;
        }

        return null;
    }

    public PasswordResetToken formPasswordResetToken(PasswordResetToken passwordResetToken, User user) {

        passwordResetToken.setToken(UUID.randomUUID().toString());
        passwordResetToken.setExpiryDate(expireDate);

        if (passwordResetToken instanceof ClientPasswordResetToken) {
            ((ClientPasswordResetToken) passwordResetToken).setClient((Client) user);
        } else if (passwordResetToken instanceof EmployeePasswordResetToken) {
            ((EmployeePasswordResetToken) passwordResetToken).setEmployee((Employee) user);
        }

        return passwordResetToken;
    }


}
