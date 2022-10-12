package com.example.tattooartistbackend;

import com.example.tattooartistbackend.generated.models.Language;
import com.example.tattooartistbackend.user.UserRepository;
import com.example.tattooartistbackend.user.elasticsearch.UserDocument;
import com.example.tattooartistbackend.user.elasticsearch.UserEsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.UUID;
import java.util.stream.Collectors;

@SpringBootApplication
@CrossOrigin
public class TattooArtistBackendApplication {

//	@Autowired
//	private  UserEsRepository userEsRepository;
//
//	@Autowired
//	private UserRepository userRepository;
	public static void main(String[] args) {
		SpringApplication.run(TattooArtistBackendApplication.class, args);
	}

//	@Bean
//	public CommandLineRunner commandLineRunner(){
//		return (args)->{
//			var authenticatedUser= userRepository.findById(UUID.fromString("93ff17c5-e52c-4c11-b8c8-a12bf982bcac")).orElseThrow();
//			var userDoc= UserDocument.builder()
//                .id(authenticatedUser.getId())
//                .hasTattooArtistAcc(authenticatedUser.isHasArtistPage())
//                .fullName(authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName())
//                .avatarUrl(authenticatedUser.getAvatarUrl())
//                .languages(authenticatedUser.getLanguages().stream().map(Language::getValue).toList())
//                .gender(authenticatedUser.getGender())
//                .city(authenticatedUser.getBusinessAddress() == null ? "" : authenticatedUser.getBusinessAddress().getCity())
//                .country(authenticatedUser.getBusinessAddress() == null ? "" : authenticatedUser.getBusinessAddress().getCountry())
//                .averageRating(authenticatedUser.getAverageRating())
//                .build();
//
//			userEsRepository.save(userDoc);
//			System.out.println("======================");
//			userEsRepository.findAll().forEach(System.out::println);
//			System.out.println("======================");
//
//		};
//	}
}
