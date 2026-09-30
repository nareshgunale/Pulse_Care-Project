//package com.ai_service.service.serviceImp;
//
//
//import com.ai_service.service.GeminiService;
//import com.google.genai.Client;
//import com.google.genai.types.GenerateContentResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class GeminiServiceImp implements GeminiService {
//
//    private final Client client;
//
//    @Override
//    public String askGemini(String prompt) {
//        String formattedPrompt = buildPrompt(prompt);
//        for (int i = 0; i < 5; i++) {
//            try {
//                GenerateContentResponse response =
//                        client.models.generateContent("gemini-2.5-flash",formattedPrompt, null);
//                return cleanResponse(response.text());
//
//            } catch (Exception e) {
//                if (e.getMessage() == null || !e.getMessage().contains("503")) {
//                    throw e;
//                }
//                try {
//                    long waitTime = (long) Math.pow(2, i) * 1000;
//                    Thread.sleep(waitTime);
//                } catch (InterruptedException ignored) {}
//            }
//        }
//        return "AI service is busy. Please try again after some time.";
//    }
//
//    private String buildPrompt(String userPrompt) {
//        return """
//            You are a medical AI assistant.
//
//            Respond in this format:
//            - Use simple bullet points
//            - Keep answers short and structured
//            - Avoid markdown symbols (** ### ``` )
//            - Add a final warning if needed
//            - Do not be verbose
//
//            User question:
//            """ + userPrompt;
//    }
//
//    private String cleanResponse(String text) {
//        if (text == null) return "";
//
//        return text
//                .replace("**", "")
//                .replace("###", "")
//                .replace("```", "")
//                .replace("\n\n", "\n")
//                .trim();
//    }
//}