package com.elitefolk.authservice.services;

import com.elitefolk.authservice.dtos.LoginSuccessDto;
import com.elitefolk.authservice.dtos.RegisterUserDto;
import com.elitefolk.authservice.exceptions.UserNotFoundException;
import com.elitefolk.authservice.models.LoginMode;
import com.elitefolk.authservice.models.Role;
import com.elitefolk.authservice.models.User;
import com.elitefolk.authservice.repositories.RoleRepository;
import com.elitefolk.authservice.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserOtpService userOtpService;

    public UserServiceImpl(UserRepository userRepo,
                           RoleRepository roleRepo,
                           PasswordEncoder passwordEncoder,
                           UserOtpService userOtpService) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
        this.userOtpService = userOtpService;
    }

    @Override
    public User registerUser(RegisterUserDto userDto) {
        String password = userDto.getPassword();
        User user = new User();
        user.setId(UUID.fromString(userDto.getUserId()));
        user.setEmail(userDto.getEmail());
        user.setMobile(userDto.getMobile());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        Role role = this.roleRepo.findByName("User").orElseGet( () -> new Role("User"));
        user.setRoles(List.of(role));
        String encryptedPassword = this.passwordEncoder.encode(password);
        user.setPassword(encryptedPassword);
        if(userDto.getLoginMode() == LoginMode.OTP) {
            this.userOtpService.generateAndSendOtp(
                    userDto.getMobile() != null ?
                            userDto.getMobile() :
                            userDto.getEmail()
            );
        }
        return this.userRepo.save(user);
    }

    @Override
    public User updateUser(String userId, LoginSuccessDto userDto) {
        return null;
    }

    @Override
    public User getUserById(String userId) {
        return null;
    }

    @Override
    public User getUserByUsername(String userName) {
        return this.userRepo.findByEmailOrMobile(userName, userName)
                .orElseThrow(() -> new UserNotFoundException("User not found", userName));
    }

    @Override
    public List<User> getAllUsers() {
        return List.of();
    }

    @Override
    public List<User> getAllUsersByPage(int page, int size) {
        return List.of();
    }
}
