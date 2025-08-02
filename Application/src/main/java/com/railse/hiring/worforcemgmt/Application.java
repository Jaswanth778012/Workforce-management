package com.railse.hiring.worforcemgmt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
	    "com.railse.hiring.workforcemgmt",
	    "com.railse.hiring.worforcemgmt"
	})

public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
