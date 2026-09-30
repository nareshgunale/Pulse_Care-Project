package com.hms.user.service;

import com.hms.user.dto.UserDTO;
import com.hms.user.exception.HMSException;

public interface UserService {

    void registerUser(UserDTO userDTO)throws HMSException;
    UserDTO loginUser(UserDTO userDTO);
    UserDTO getUserById(Long id);
    void updateUser(UserDTO userDTO);
    UserDTO getUser(String email);
    void createDoctor(UserDTO userDTO);
}
