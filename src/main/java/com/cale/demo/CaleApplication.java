package com.cale.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

@SpringBootApplication()
//@EntityScan(basePackages = "com.cale.demo.models")
public class CaleApplication {

	public static void main(String[] args) {
		SpringApplication.run(CaleApplication.class, args);
	}

}
