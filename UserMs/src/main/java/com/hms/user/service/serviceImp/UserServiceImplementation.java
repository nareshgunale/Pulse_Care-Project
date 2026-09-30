package com.hms.user.service.serviceImp;

import com.hms.user.constant.Roles;
import com.hms.user.clients.ProfileClients;
import com.hms.user.dto.UserDTO;
import com.hms.user.entity.User;
import com.hms.user.exception.HMSException;
import com.hms.user.repository.UserRepository;
import com.hms.user.service.ApiService;
import com.hms.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service("userService")
@Transactional
@RequiredArgsConstructor
public class  UserServiceImplementation implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImplementation.class);


    private final UserRepository repository;

    private final PasswordEncoder passwordEncoder;

    private final ApiService apiService;

    private final ProfileClients profileClients;

//    @Override
//    public void registerUser(UserDTO userDTO) {
//        log.info("Registering user with email: {}", userDTO.getEmail());
//        Optional<User> byEmail = repository.findByEmail(userDTO.getEmail());
//        if (byEmail.isPresent()) {
//            log.warn("User already exists with email: {}", userDTO.getEmail());
//            throw new HMSException("USER_ALREADY_EXITS");
//        }
//        log.debug("Encoding password for email: {}", userDTO.getEmail());
//        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
//        log.info("Calling Profile Service to create profile for email: {}", userDTO.getEmail());
//        Long profileId = null;
//        if (userDTO.getRole().equals(Roles.DOCTOR)) {
//            profileId = profileClients.addDoctor(userDTO);
//        } else if (userDTO.getRole().equals(Roles.PATIENT)) {
//            profileId = profileClients.addPatient(userDTO);
//        }
//        userDTO.setProfileId(profileId);
//        repository.save(userDTO.toEntity());
//        log.info("User saved successfully in database with email: {}", userDTO.getEmail());
//    }

    /// role based

    @Override
    public void registerUser(UserDTO userDTO) {
        Optional<User> byEmail = repository.findByEmail(userDTO.getEmail());
        if (byEmail.isPresent()) {
            throw new HMSException("USER_ALREADY_EXITS");
        }
        userDTO.setRole(Roles.PATIENT);
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        // First save user to generate ID
        User savedUser = repository.save(userDTO.toEntity());
        userDTO.setId(savedUser.getId());

        // Now call ProfileMS with correct userId
        Long profileId = profileClients.addPatient(userDTO);

        // Update profileId back on user
        savedUser.setProfileId(profileId);
        repository.save(savedUser);
    }

    @Override
    public UserDTO loginUser(UserDTO userDTO) {
        log.info("Login attempt for email: {}", userDTO.getEmail());
        User user = repository.findByEmail(userDTO.getEmail())
                .orElseThrow(() -> {
                    log.warn("User not found for email: {}", userDTO.getEmail());
                    return new HMSException("USER_NOT_FOUND");
                });
        if (!passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
            log.warn("Invalid password attempt for email: {}", userDTO.getEmail());
            throw new HMSException("INVALID_CREDENTAILS");
        }
        log.info("User login successful for email: {}", userDTO.getEmail());
        user.setPassword(null);
        return user.toDTO();
    }

    @Override
    public UserDTO getUserById(Long id) {
        log.info("Fetching user by id: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", id);
                    return new HMSException("USER_NOT_FOUND");
                })
                .toDTO();
    }

    @Override
    public void updateUser(UserDTO userDTO) {

    }

    @Override
    public UserDTO getUser(String email) {
        log.info("Fetching user by email: {}", email);
        return repository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", email);
                    return new HMSException("USER_NOT_FOUND");
                })
                .toDTO();
    }

    @Override
    public void createDoctor(UserDTO userDTO) {
        Optional<User> byEmail = repository.findByEmail(userDTO.getEmail());
        if (byEmail.isPresent()) {
            throw new HMSException("USER_ALREADY_EXITS");
        }
        //Set Role and password
        userDTO.setRole(Roles.DOCTOR);
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        //First User save
        User savedUser = repository.save(userDTO.toEntity());
        userDTO.setId(savedUser.getId());

        //Send to ProfileMS
        Long profileId = profileClients.addDoctor(userDTO);

        //Update ProfileId
        savedUser.setProfileId(profileId);
        repository.save(savedUser);
    }
}
