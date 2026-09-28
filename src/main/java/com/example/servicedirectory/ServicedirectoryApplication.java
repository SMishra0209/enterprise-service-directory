package com.example.servicedirectory;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServicedirectoryApplication {

	public static void main(String[] args) {
		// Run in UTC so the DB driver never sends a legacy timezone name like "Asia/Calcutta"
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		SpringApplication.run(ServicedirectoryApplication.class, args);
	}
}