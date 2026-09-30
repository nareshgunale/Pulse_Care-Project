package com.hms.appointment;

import com.hms.appointment.config.AwsSecretsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@EnableAsync
public class AppointmentApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(AppointmentApplication.class);
		app.addInitializers(new AwsSecretsConfig());
		app.run(args);
	}

}
