package com.elitefolk.authservice.services;

import com.elitefolk.authservice.dtos.RegisterUserDto;
import com.elitefolk.authservice.dtos.LoginSuccessDto;
import com.elitefolk.authservice.models.LoginMode;
import com.elitefolk.authservice.models.User;

import java.util.List;

public interface UserService {
    User registerUser(RegisterUserDto userDto);
    User updateUser(String userId, LoginSuccessDto userDto);
    User getUserById(String userId);
    User getUserByUsername(String userName);
    List<User> getAllUsers();
    List<User> getAllUsersByPage(int page, int size);
}
