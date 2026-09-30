package com.hms.pharmacy;

import com.hms.pharmacy.config.AwsSecretsConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PharmacyMsApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(PharmacyMsApplication.class);
		app.addInitializers(new AwsSecretsConfig());
		app.run(args);
	}

}
