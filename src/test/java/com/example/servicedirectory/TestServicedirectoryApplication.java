package com.example.servicedirectory;

import org.springframework.boot.SpringApplication;

public class TestServicedirectoryApplication {

	public static void main(String[] args) {
		SpringApplication.from(ServicedirectoryApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
