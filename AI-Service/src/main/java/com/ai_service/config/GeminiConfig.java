package com.ai_service.config;

import com.google.genai.Client;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GeminiThinkingConfig;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures all Gemini AI and LangChain4j components used by the application.
 * - A Spring configuration class that creates and manages AI-related beans.

 * Why ?
 * - Centralizes all Gemini and LangChain4j configuration in one place.
 * - Spring creates these objects once and injects them wherever they are needed.
 * - Avoids creating AI clients and models multiple times.
 * - Gemini Client: Used to communicate with the Google Gemini API.
 * - ChatLanguageModel: The LangChain4j model that sends prompts and receives AI responses.
 * - ChatMemoryProvider: Creates a separate conversation memory for each user.

 * How work?
 * - Spring Boot loads this class at application startup.
 * - Methods annotated with @Bean create singleton objects.
 * - These beans are automatically injected into other classes using dependency injection.
 */

@Configuration
public class GeminiConfig {

    @Value("${gemini.api.key}")
    private String apiKey;

    /**
     * Creates the Google Gemini API client.
     * Used for direct communication with the Gemini API when required.
     */
    @Bean
    public Client geminiClient() {
        return Client.builder()
                .apiKey(apiKey)
                .build();
    }

    /**
     * Creates the LangChain4j chat model.
     * multipart (thought + functionCall) responses that require a
     * "thought_signature". This fixes the crash when RAG context + a tool
     * call (e.g. getMyAppointments) happen in the same turn.
     */
    @Bean
    public ChatModel chatLanguageModel() {
        return GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-3.6-flash")
                .build();
    }

    /**
     * Creates chat memory for each user.
     * - Allows the AI to remember previous messages in the same conversation.
     * - Each user gets a separate MessageWindowChatMemory using their memoryId.
     * - Only the last 10 messages are stored to provide conversation context.
     */
    @Bean
    public ChatMemoryProvider chatMemoryProvider() {
        return memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(10)
                .build();
    }
}