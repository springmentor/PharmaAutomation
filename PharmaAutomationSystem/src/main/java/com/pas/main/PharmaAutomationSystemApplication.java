package com.pas.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EntityScan("com.pas.model")
@ComponentScan(basePackages={"com.*"})
@EnableJpaRepositories("com.pas.repository")
@EnableScheduling
public class PharmaAutomationSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(PharmaAutomationSystemApplication.class, args);
	}

}