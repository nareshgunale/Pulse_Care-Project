package com.hms.user.service.serviceImp;

import com.hms.user.dto.ChangePassword;
import com.hms.user.dto.MailBody;
import com.hms.user.entity.ForgotPassword;
import com.hms.user.entity.User;
import com.hms.user.exception.HMSException;
import com.hms.user.repository.ForgotPasswordRepository;
import com.hms.user.repository.UserRepository;
import com.hms.user.service.EmailService;
import com.hms.user.service.ForgotPasswordService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

    private final UserRepository userRepository;

    private final EmailService emailService;

    private final ForgotPasswordRepository forgotPasswordRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public String verifyEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new HMSException("Please Enter valid email"));

        Long otp = otpGenerator();
        Date expirationTime = new Date(System.currentTimeMillis() + 2 * 60 * 1000);

        MailBody mailBody = MailBody.builder()
                .to(email)
                .text("This is the OTP for Forgot Password : " + otp)
                .subject("OTP for Forgot Password")
                .build();

        // UPDATE existing record if present otherwise INSERT new
        ForgotPassword forgotPassword = forgotPasswordRepository.findByUser(user)
                .map(existing -> {
                    existing.setOtp(otp);
                    existing.setExpirationTime(expirationTime);
                    return existing;
                })
                .orElse(ForgotPassword.builder()
                        .otp(otp)
                        .expirationTime(expirationTime)
                        .user(user)
                        .build());

        emailService.sendSimpleMessage(mailBody);
        forgotPasswordRepository.save(forgotPassword);
        return "Email Sent for verification";
    }

    @Override
    public String verifyOtp(Long otp, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new HMSException("Please Enter valid email"));

        ForgotPassword forgotPassword = forgotPasswordRepository.findByOtpAndUser(otp, user)
                .orElseThrow(() ->
                        new HMSException("Invalid OTP for email : " + email));

        if (forgotPassword.getExpirationTime().before(Date.from(Instant.now()))) {
            forgotPasswordRepository.deleteById(forgotPassword.getId());
            throw new HMSException("OTP has expired!");
        }
        return "OTP verified";
    }

    @Override
    public String changePassword(ChangePassword changePassword,
                                 String email) {

        if (!Objects.equals(changePassword.getPassword(), changePassword.getConfirmPassword())) {
            throw new HMSException("Please Enter Password again");
        }

        String encodedPassword =
                passwordEncoder.encode(changePassword.getPassword());
        userRepository.updatePassword(email, encodedPassword);
        return "Password has been changed!";
    }

    private Long otpGenerator() {
        Random random = new Random();
        return random.nextLong(100000, 999999);
    }
}