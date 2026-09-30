package com.ai_service.controller;


import com.ai_service.dto.AIRequest;
import com.ai_service.service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * - Provides an API endpoint for users to interact with Gemini AI.
 * - This chatbot handles general healthcare-related questions.
 * - Provides simple AI responses without requiring user authentication,
 *   patient data, or application tools.
 * - Keeps general AI conversations separate from the AI Agent flow.

 * How does it work?
 * - Receives the user's question through the API request.
 * - Sends the question to GeminiService.
 * - GeminiService communicates with the Gemini model and generates a response.
 * - Returns the AI-generated response back to the user.

 * Flow:
 * User → GeminiController → GeminiService → Gemini Model → Response
 */
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GeminiController {

    private final GeminiService geminiService;

//    @PostMapping("/ask")
//    public ResponseEntity<String> askGemini(@RequestBody AIRequest request){
//        String response = geminiService.askGemini(request.getQuestion());
//        return ResponseEntity.ok(response);
//    }

    /**
     * Handles public chatbot questions.

     * This endpoint does not use user context, JWT, or AI tools.
     * It is only used for general AI conversations where dynamic
     * application data is not required.
     */
    @PostMapping("/ask")
    public ResponseEntity<String> askGemini(@RequestBody AIRequest request){
        String response = geminiService.askGemini(request.getQuestion());
        return ResponseEntity.ok(response);
    }
}