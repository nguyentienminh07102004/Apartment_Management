package com.ptitB22CN539.LaptopShop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableJpaAuditing
public class LaptopShopApplication {

	public static void main(String[] args) {
		SpringApplication.run(LaptopShopApplication.class, args);
	}

}
