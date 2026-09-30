package com.ai_service.service.serviceImp;

import com.ai_service.service.GeminiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiServiceImp2 implements GeminiService {

    private final GeminiProvider geminiProvider;
    private final GroqProvider groqProvider;

    @Override
    public String askGemini(String prompt) {

        String formattedPrompt = buildPrompt(prompt);

        // Try Gemini first
        for (int i = 0; i < 5; i++) {

            try {

                log.info("Trying Gemini...");

                return cleanResponse(
                        geminiProvider.ask(formattedPrompt)
                );

            } catch (Exception e) {

                log.error("Gemini Failed: {}", e.getMessage());

                // Try Groq if Gemini fails
                try {

                    log.info("Trying Groq...");

                    return cleanResponse(
                            groqProvider.ask(formattedPrompt)
                    );

                } catch (Exception ex) {

                    log.error("Groq Failed: {}", ex.getMessage());
                }
            }
        }

        return "All AI services are currently unavailable.";
    }

    private String buildPrompt(String userPrompt) {

        return """
            You are a medical AI assistant.

            Rules:
            - Use bullet points
            - Keep answers short
            - No markdown
            - Avoid long explanations

            User Question:
            """ + userPrompt;
    }

    private String cleanResponse(String text) {

        if (text == null) {
            return "";
        }

        return text
                .replace("**", "")
                .replace("###", "")
                .replace("```", "")
                .replace("\n\n", "\n")
                .trim();
    }
}