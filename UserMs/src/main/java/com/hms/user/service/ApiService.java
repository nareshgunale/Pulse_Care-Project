package com.hms.user.service;

import com.hms.user.constant.Roles;
import com.hms.user.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ApiService {

    @Autowired
    private WebClient.Builder webClient;

    public Mono<Long> addProfile(UserDTO userDTO) {
        if (userDTO.getRole().equals(Roles.DOCTOR)) {
            return webClient.build().post()
                    .uri("http://localhost:9100/profile/doctor/add")
                    .bodyValue(userDTO)
                    .retrieve()
                    .bodyToMono(Long.class);
        }else if (userDTO.getRole().equals(Roles.PATIENT)) {
            return webClient.build().post()
                    .uri("http://localhost:9100/profile/patient/add")
                    .bodyValue(userDTO)
                    .retrieve()
                    .bodyToMono(Long.class);
        }
        return null;
    }
}

//Mono and WebClient is the standard way to handle communication between services
// because they are non-blocking and resource-efficient.

//In a traditional "blocking" system (like RestTemplate), when your service makes
//a request to localhost:8082, the worker thread sits idle and waits for the response.
//
//With WebClient & Mono: The thread that starts the request
//is immediately released to handle other tasks (like another user logging in).
//
//The Result: Your system can handle thousands of concurrent requests using only
//a small number of threads, significantly reducing memory usage.
