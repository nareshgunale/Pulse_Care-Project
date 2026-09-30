package com.hms.appointment.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "AI-SERVICE")
public interface AiServiceClient {

    @PostMapping("/ai/rag/ingest-prescriptions/{patientId}")
    void reindexPrescriptions(@PathVariable Long patientId);
}