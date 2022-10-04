package com.example.tattooartistbackend;

import com.example.tattooartistbackend.user.elasticsearch.UserEsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@CrossOrigin
public class TattooArtistBackendApplication {

	@Autowired
	private  UserEsRepository userEsRepository;

	public static void main(String[] args) {
		SpringApplication.run(TattooArtistBackendApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(){
		return (args)->{
//			userEsRepository.deleteAll();
		};
	}
}
