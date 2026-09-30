package com.ai_service.service.serviceImp;

import com.ai_service.service.AIProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

//@Service
@RequiredArgsConstructor
public class OpenRouterProvider implements AIProvider {

    private final WebClient webClient;

    @Value("${openrouter.api.key}")
    private String apiKey;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String ask(String prompt) {

        Map<String, Object> body = Map.of(
                "model", "deepseek/deepseek-chat:free",
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        String response = webClient.post()
                .uri("https://openrouter.ai/api/v1/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {

            JsonNode json = mapper.readTree(response);

            return json
                    .get("choices")
                    .get(0)
                    .get("message")
                    .get("content")
                    .asText();

        } catch (Exception e) {
            throw new RuntimeException("OpenRouter parsing failed");
        }
    }
}