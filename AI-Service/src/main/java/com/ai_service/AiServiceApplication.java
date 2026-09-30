package com.ai_service;

import com.ai_service.config.AwsSecretsConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AiServiceApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(AiServiceApplication.class);
		app.addInitializers(new AwsSecretsConfig());
		app.run(args);
	}

}
