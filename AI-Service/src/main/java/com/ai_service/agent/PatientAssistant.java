package com.ai_service.agent;


import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

/**
 * PatientAssistant is the main AI Agent interface.
 * Defines how the application communicates with the AI model.
 * Keeps AI-related logic separate from controllers and services.

 * How it works?

 * @AiService creates the implementation automatically (responsible Langchain4j).
 * @SystemMessage sets the AI's behavior and rules.
 * @MemoryId keeps conversation context for each user (like Memory).
 * @UserMessage sends the user's question to the AI.
 * LangChain4j combines the prompt, memory, and tools to generate a response.
 */

@AiService
public interface PatientAssistant {


    /**
     * Example:
     * User: "Show my appointments"
     * AI checks available tools → calls AppointmentTool → returns response.
     * <p>
     * userId unique identifier used for maintaining user-specific memory
     * message user's question or request
     * AI-generated response
     */

    @SystemMessage("""
            You are Pulse AI Developed by Avinash Surwase.
            
            You help authenticated patients with healthcare related tasks.
            
            You have two sources of extra information:
            1. Tools — for live, patient-specific data (appointments, bookings, slots).
            2. Retrieved knowledge base context — for the patient's own past
               prescriptions (medicines, dosage, doctor's notes) and general
               info like health packages and hospital policies.
            
            Rules:
            1. Always ys protect patient privacy.
            2. Never ask users for patient ID.
            3. Use available tools when live data is required.
            4. Use retrieved knowledge base context for prescription history
               and general questions.
            5. Only answer prescription questions using retrieved context that
               belongs to the currently logged-in patient. Never reveal another
               patient's prescription details.
            6. If the answer is not found in tools or retrieved context, say
               you don't have that information — do not guess or make it up.
            7. Do not guess medical information.
            8. For medical advice, provide general information and recommend consulting a doctor.
            9. Keep responses simple and easy to understand.
            """)
    String chat(@MemoryId String userId, @UserMessage String message);
}
