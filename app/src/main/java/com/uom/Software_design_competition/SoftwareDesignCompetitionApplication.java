package com.uom.Software_design_competition;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


// @SpringBootApplication(exclude = { 
//     org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class 
// })
@SpringBootApplication
public class SoftwareDesignCompetitionApplication {

	public static void main(String[] args) {
		SpringApplication.run(SoftwareDesignCompetitionApplication.class, args);
	}

}
