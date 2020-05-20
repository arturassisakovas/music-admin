package com.mAdmin.service;

import com.mAdmin.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;


public class UserServiceImpl implements UserService {

    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean checkCurrentPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public boolean changePassword(User user, String rawPassword) {
        return false;
    }

    @Override
    public List<User> getAsUsersList(List<? extends User> users) {
        if (users == null) {
            return new ArrayList<>();
        }
        List<User> baseUsers = new ArrayList<>(users);
        return baseUsers;
    }
}
