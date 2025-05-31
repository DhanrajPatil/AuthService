package com.elitefolk.authservice.services;

public interface UserOtpService {
    Boolean generateAndSendOtp(String userName);
    Boolean verifyOtp(String userName, Integer otp);
}
