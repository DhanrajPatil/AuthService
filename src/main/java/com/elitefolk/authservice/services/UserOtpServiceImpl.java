package com.elitefolk.authservice.services;

import com.elitefolk.authservice.aws.AwsEmailService;
import com.elitefolk.authservice.models.UserOtp;
import com.elitefolk.authservice.repositories.UserOtpRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserOtpServiceImpl implements UserOtpService {
    // This class is responsible for generating and verifying OTPs for users.
    // It uses the UserOtpRepository to interact with the database.

    private final UserOtpRepository userOtpRepository;
    private final AwsEmailService awsEmailService;
    private final PasswordEncoder passwordEncoder;

    public UserOtpServiceImpl(UserOtpRepository userOtpRepository,
                               AwsEmailService awsEmailService,
                               PasswordEncoder passwordEncoder) {
        this.userOtpRepository = userOtpRepository;
        this.awsEmailService = awsEmailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Boolean generateAndSendOtp(String userName) {
        // Logic to generate OTP
        // Save OTP to the database using userOtpRepository
        // Send OTP to the user via email using awsEmailService
        try {
            Long expiryTime = System.currentTimeMillis() + 5 * 60 * 1000; // 5 minutes expiry
            Integer otp = (int) (Math.random() * 900000) + 100000; // Generate a 6-digit OTP
            // Save OTP to the database
            String encryptedOtp = passwordEncoder.encode(otp.toString());
                // Update existing OTP
            UserOtp userOtp = userOtpRepository.findByUserNameAndIsVerified(userName, false).orElse(null);
            if(userOtp != null) {
                userOtp.setOtp(encryptedOtp);
                userOtp.setExpiryTime(expiryTime);
                userOtpRepository.save(userOtp);
            } else {
                // Create new OTP entry
                userOtp = new UserOtp(userName, encryptedOtp, expiryTime, false);
                userOtpRepository.save(userOtp);
            }

            if (userName.contains("@")) {
                // If userName is an email
                this.sendOtpViaEmail(userName, otp);
            } else {
                // If userName is a mobile number
                this.sendOtpViaSms(userName, otp);
            }
        } catch (Exception e) {
            // Handle exception
            System.out.println("Error generating or sending OTP: " + e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public Boolean verifyOtp(String userName, Integer otp) {
        UserOtp userOtp = userOtpRepository.findByUserNameAndIsVerified(userName, false)
                .orElseThrow(() -> new RuntimeException("for this user, OTP not found or already verified"));
        if (userOtp != null) {
            String encryptedOtp = userOtp.getOtp();
            String otpString = otp.toString();
            if(userOtp.getExpiryTime() < System.currentTimeMillis()) {
                // OTP has expired
                return false;
            }
            boolean bl = this.passwordEncoder.matches(otpString, encryptedOtp);
            if (bl) {
                // OTP is valid, mark it as verified
                userOtp.setIsVerified(true);
                userOtpRepository.save(userOtp);
                return true;
            }
        }
        return false;
    }

    public void sendOtpViaEmail(String email, Integer otp) {
        String subject = "Your OTP Code";
        String body = "Your 6 Digit OTP code is: " + otp + ". It is valid for 5 minutes.";
        // Send OTP to the user's email
        awsEmailService.sendEmail(subject, body, email);
    }

    public void sendOtpViaSms(String mobile, Integer otp) {
        // Logic to send OTP via SMS
        // For example, using an SMS service provider's API
    }
}
