package com.ai_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "PROFILEMS")
public interface ProfileClient {

    @GetMapping("/profile/patient/patient-id/{userId}")
    Long getPatientIdByUserId(@PathVariable Long userId);

}
