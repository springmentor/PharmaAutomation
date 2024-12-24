package com.pms.drug;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@ComponentScan(basePackages = {"com.pms"})
@EntityScan("com.pms.model")
@EnableJpaRepositories("com.pms.repository")
@EnableScheduling
public class PharmaAutomationSystem1Application {

	public static void main(String[] args) {
		SpringApplication.run(PharmaAutomationSystem1Application.class, args);
	}

}
