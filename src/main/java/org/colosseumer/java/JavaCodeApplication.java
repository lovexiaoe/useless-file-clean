package org.colosseumer.java;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class JavaCodeApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavaCodeApplication.class, args);
	}

}