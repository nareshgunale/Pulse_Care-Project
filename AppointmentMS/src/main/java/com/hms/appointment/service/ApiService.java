package com.hms.appointment.service;

import com.hms.appointment.dto.DoctorDTO;
import com.hms.appointment.dto.PatientDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ApiService {

    @Autowired
    private WebClient.Builder webClient;

    public Mono<Boolean> doctorExists(Long id) {
        return webClient.build().get()
                .uri("http://localhost:9100/profile/doctor/exists/" + id)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<Boolean> patientExists(Long id) {
        return webClient.build().get()
                .uri("http://localhost:9100/profile/patient/exists/" + id)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<PatientDTO> getPatientById(Long id) {
        return webClient.build().get()
                .uri("http://localhost:9100/profile/patient/get/" + id)
                .retrieve()
                .bodyToMono(PatientDTO.class);
    }

    public Mono<DoctorDTO> getDoctorById(Long id) {
        return webClient.build().get()
                .uri("http://localhost:9100/profile/doctor/get/" + id)
                .retrieve()
                .bodyToMono(DoctorDTO.class);
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
