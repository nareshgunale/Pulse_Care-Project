package com.hms.gateway;

import com.hms.gateway.config.AwsSecretsConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GatewayMsApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(GatewayMsApplication.class);
		app.addInitializers(new AwsSecretsConfig());
		app.run(args);
	}

}
