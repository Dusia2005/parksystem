package com.project.parksystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class ParksystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(ParksystemApplication.class, args);
	}

}
