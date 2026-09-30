package com.hms.user.controller;

import com.hms.user.dto.LoginDTO;
import com.hms.user.dto.ResponseDTO;
import com.hms.user.dto.UserDTO;
import com.hms.user.exception.HMSException;
import com.hms.user.jwt.JwtUtil;
import com.hms.user.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> registerUser(@RequestBody @Valid UserDTO userDTO) throws HMSException {
        log.info("Received request to register user with email: {}", userDTO.getEmail());
        userService.registerUser(userDTO);
        log.info("User registered successfully with email: {}", userDTO.getEmail());
        return new ResponseEntity<>(new ResponseDTO("Register Successfully."), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginDTO loginDTO) throws HMSException {
        log.info("Login attempt for email: {}", loginDTO.getEmail());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getEmail(),
                            loginDTO.getPassword())
            );
            log.info("Authentication successful for email: {}", loginDTO.getEmail());
        } catch (AuthenticationException e) {
            log.warn("Invalid login attempt for email: {}", loginDTO.getEmail());
            throw new HMSException("INVALID_CREDENTIALS");
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginDTO.getEmail());
        log.debug("User details loaded for email: {}", loginDTO.getEmail());
        String jwt = jwtUtil.generateToken(userDetails);
        log.info("JWT token generated for email: {}", loginDTO.getEmail());
        return new ResponseEntity<>(jwt, HttpStatus.OK);
    }

    @PostMapping("/create-doctor")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO> createDoctor(@RequestBody @Valid UserDTO userDTO) {
        userService.createDoctor(userDTO);
        return new ResponseEntity<>(new ResponseDTO("Doctor Created Successfully"), HttpStatus.CREATED);
    }
}
