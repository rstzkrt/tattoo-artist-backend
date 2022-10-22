package com.example.tattooartistbackend;

import com.example.tattooartistbackend.generated.models.Language;
import com.example.tattooartistbackend.tattooWork.TattooWork;
import com.example.tattooartistbackend.tattooWork.TattooWorkRepository;
import com.example.tattooartistbackend.tattooWork.elasticsearch.TattooWorkDocument;
import com.example.tattooartistbackend.tattooWork.elasticsearch.TattooWorkEsRepository;
import com.example.tattooartistbackend.user.UserRepository;
import com.example.tattooartistbackend.user.elasticsearch.UserDocument;
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
//
//	@Autowired
//	private  UserEsRepository userEsRepository;
//
//	@Autowired
//	private UserRepository userRepository;
//
//	@Autowired
//	private TattooWorkRepository tattooWorkRepository;
//
//	@Autowired
//	private TattooWorkEsRepository tattooWorkEsRepository;

	public static void main(String[] args) {
		SpringApplication.run(TattooArtistBackendApplication.class, args);
	}
//	@Bean
//	public CommandLineRunner commandLineRunner(){
//		return (args)->{
//			userRepository.findAll().forEach(user -> {
//				var userDoc= UserDocument.builder()
//						.id(user.getId())
//						.hasTattooArtistAcc(user.isHasArtistPage())
//						.fullName(user.getFirstName() + " " + user.getLastName())
//						.avatarUrl(user.getAvatarUrl())
//						.languages(user.getLanguages().stream().map(Language::getValue).collect(Collectors.toList()))
//						.gender(user.getGender())
//						.city(user.getBusinessAddress() == null ? "" : user.getBusinessAddress().getCity())
//						.country(user.getBusinessAddress() == null ? "" : user.getBusinessAddress().getCountry())
//						.averageRating(user.getAverageRating())
//						.build();
//				userEsRepository.save(userDoc);
//			});
//			tattooWorkRepository.findAll().forEach(tattooWork -> {
//				tattooWorkEsRepository.save(TattooWorkDocument.fromTattooWork(tattooWork));
//			});
//		};
//	}
}
