package com.example.tattooartistbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@CrossOrigin
public class TattooArtistBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(TattooArtistBackendApplication.class, args);
	}

}
