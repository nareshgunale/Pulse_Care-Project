package com.ai_service.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.util.Map;

public class AwsSecretsConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(AwsSecretsConfig.class);

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        try {
            SecretsManagerClient client = SecretsManagerClient.builder()
                    .region(Region.AP_SOUTH_1)
                    .build();

            // Common database credentials
            loadSecret(client, "/myapp/common/db-credentials", context);

            // Gemini API key
            loadSecret(client, "/myapp/ai-service/gemini-key", context);

            // Qdrant credentials
            loadSecret(client, "/myapp/ai-service/qdrant-credentials", context);
            // Groq API key
            loadSecret(client, "/myapp/ai-service/groq-key", context);

            client.close();

            log.info("AWS Secrets loaded successfully!");

        } catch (Exception e) {
            log.error("AWS Secrets load failed: {}", e.getMessage());
            throw new RuntimeException("Failed to load AWS secrets", e);
        }
    }

    private void loadSecret(
            SecretsManagerClient client,
            String secretName,
            ConfigurableApplicationContext context) {

        try {
            GetSecretValueRequest request = GetSecretValueRequest.builder()
                    .secretId(secretName)
                    .build();

            GetSecretValueResponse response = client.getSecretValue(request);

            Map<String, Object> secrets = objectMapper.readValue(
                    response.secretString(),
                    new TypeReference<>() {
                    }
            );

            context.getEnvironment()
                    .getPropertySources()
                    .addFirst(
                            new MapPropertySource(secretName, secrets)
                    );

        } catch (Exception e) {
            log.error("Failed to load secret: {}", secretName);
            throw new RuntimeException(e);
        }
    }
}