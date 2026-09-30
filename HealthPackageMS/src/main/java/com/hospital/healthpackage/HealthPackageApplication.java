package com.hospital.healthpackage;

import com.hospital.healthpackage.config.AwsSecretsConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class HealthPackageApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(HealthPackageApplication.class);
        app.addInitializers(new AwsSecretsConfig());
        app.run(args);
    }
}
