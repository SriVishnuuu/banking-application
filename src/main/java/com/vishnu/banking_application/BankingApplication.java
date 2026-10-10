package com.vishnu.banking_application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableScheduling
public class BankingApplication {

	public static void main(String[] args) {

		SpringApplication.run(BankingApplication.class, args);
	}

}
