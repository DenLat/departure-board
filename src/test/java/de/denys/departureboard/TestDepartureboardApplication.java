package de.denys.departureboard;

import org.springframework.boot.SpringApplication;

public class TestDepartureboardApplication {

	public static void main(String[] args) {
		SpringApplication.from(DepartureboardApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
