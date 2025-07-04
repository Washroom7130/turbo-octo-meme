package com.api.Event_Management_API;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.api.Event_Management_API.service.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAsync
@ComponentScan(basePackages = "com.api.Event_Management_API")
@EnableJpaRepositories(basePackages = "com.api.Event_Management_API.repository")
@EntityScan(basePackages = "com.api.Event_Management_API.model")
@EnableScheduling
public class EventManagementApiApplication /*implements CommandLineRunner*/ {

	// @Autowired
    // private EmailService emailService;
	public static void main(String[] args) {
		EnvLoader.loadEnv(); // <- load env values
		SpringApplication.run(EventManagementApiApplication.class, args);
	}

	// Test purpose only (uncomment if want to test)
	// @Override
	// public void run(String... args) {
	// 	emailService.sendTestEmail();
	// }

}
