package com.example.profile;

import com.example.profile.config.AwsSecretsConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProfileMsApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(ProfileMsApplication.class);
		app.addInitializers(new AwsSecretsConfig());
		app.run(args);
	}

}
