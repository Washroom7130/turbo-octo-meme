package com.api.Event_Management_API;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EventManagementApiApplication {

	public static void main(String[] args) {
		EnvLoader.loadEnv(); // <- load env values
		SpringApplication.run(EventManagementApiApplication.class, args);
	}

}
