package com.process.archivalservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ArchivalserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArchivalserviceApplication.class, args);
	}

}
