package com.hms.user.service;

import com.hms.user.dto.ChangePassword;

public interface ForgotPasswordService {
    String verifyEmail(String email);

    String verifyOtp(Long otp, String email);

    String changePassword(ChangePassword changePassword, String email);
}
