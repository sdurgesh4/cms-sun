package com.inturn.suncomputer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SuncomputerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SuncomputerApplication.class, args);
	}

}
