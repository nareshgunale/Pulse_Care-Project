package com.ai_service.controller;

import com.ai_service.agent.PatientAssistant;
import com.ai_service.agent.UserContext;
import com.ai_service.dto.AgentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * - Acts as the entry point for AI chat requests.
 * - Receives the user's question, identifies the authenticated user,
 * and forwards the request to the AI agent.
 * How work?
 * - Receives the user's ID from the request header and the question from
 * the request body.
 * - Stores the user ID in UserContext so AI tools can access it without
 * passing it through every method.
 * - Calls PatientAssistant to process the user's question.
 * - Returns the AI-generated response to the client.
 * - Finally, clears UserContext to prevent data from leaking into
 * another request.
 */

@RestController
@RequestMapping("/api/patient-ai")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PatientAIController {

    private final PatientAssistant patientAssistant;

    /**
     * Processes a patient's chat request and returns an AI-generated response.
     * Request Flow:
     * Client → Controller → UserContext → PatientAssistant → AI Model/Tools → Response
     */
    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestHeader("userId") String userId, @RequestBody AgentRequest request) {
        try {

            // Store the authenticated user's ID for the current request.
            // AI tools can access it using UserContext.getUserId().

            UserContext.setUserId(Long.parseLong(userId));
            String response = patientAssistant.chat(userId, request.getQuestion());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } finally {

            // Always clear the ThreadLocal after the request finishes
            // because to prevent memory leaks and accidental reuse by another request.

            UserContext.clear();
        }
    }
}