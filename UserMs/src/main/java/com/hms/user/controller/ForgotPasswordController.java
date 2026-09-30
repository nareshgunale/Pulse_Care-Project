package com.hms.user.controller;

import com.hms.user.dto.ChangePassword;
import com.hms.user.service.ForgotPasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/forgotPassword")
@RequiredArgsConstructor
public class ForgotPasswordController {

    private final ForgotPasswordService forgotPasswordService;

    @PostMapping("/verifyMail/{email}")
    public ResponseEntity<String> verifyEmail(@PathVariable String email) {
        return ResponseEntity.ok(forgotPasswordService.verifyEmail(email));
    }

    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<String> verifyOtp(@PathVariable Long otp, @PathVariable String email) {
        return ResponseEntity.ok(forgotPasswordService.verifyOtp(otp, email));
    }

    @PostMapping("/changePassword/{email}")
    public ResponseEntity<String> changePasswordHandler(@RequestBody ChangePassword changePassword, @PathVariable String email) {
        return ResponseEntity.ok(forgotPasswordService.changePassword(changePassword, email)
        );
    }
}