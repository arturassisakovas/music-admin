package com.mAdmin.service;

import com.mAdmin.entity.User;

import java.util.List;


public interface UserService {

    
    boolean checkCurrentPassword(String rawPassword, String encodedPassword);

    
    boolean changePassword(User user, String rawPassword);

    
    List<User> getAsUsersList(List<? extends User> users);

}
