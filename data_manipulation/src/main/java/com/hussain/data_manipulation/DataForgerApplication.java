package com.hussain.data_manipulation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class DataForgerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataForgerApplication.class, args);
	}

}
