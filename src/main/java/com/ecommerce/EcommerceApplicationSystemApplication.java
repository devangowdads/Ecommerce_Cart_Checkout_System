package com.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class EcommerceApplicationSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommerceApplicationSystemApplication.class, args);
	}

}
