package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@org.springframework.context.annotation.Bean
	public org.springframework.boot.CommandLineRunner demo(com.example.demo.repository.CategoryRepository repository) {
		return (args) -> {
			if (repository.count() == 0) {
				com.example.demo.model.Category c1 = new com.example.demo.model.Category();
				c1.setName("Điện thoại");
				repository.save(c1);

				com.example.demo.model.Category c2 = new com.example.demo.model.Category();
				c2.setName("Laptop");
				repository.save(c2);

				com.example.demo.model.Category c3 = new com.example.demo.model.Category();
				c3.setName("Đồng hồ");
				repository.save(c3);
			}
		};
	}
}


