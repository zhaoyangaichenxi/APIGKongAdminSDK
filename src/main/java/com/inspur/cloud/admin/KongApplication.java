package com.inspur.cloud.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@ComponentScan("com.inspur")
@ServletComponentScan("com.inspur")
public class KongApplication {

	public static void main(String[] args) {

		SpringApplication.run(KongApplication.class, args);

	}

}
