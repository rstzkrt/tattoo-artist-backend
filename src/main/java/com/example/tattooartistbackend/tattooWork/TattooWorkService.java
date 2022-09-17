package com.example.tattooartistbackend.tattooWork;

import com.example.tattooartistbackend.exceptions.*;

import com.example.tattooartistbackend.generated.models.Currency;
import com.example.tattooartistbackend.generated.models.TattooWorkPatchRequestDto;
import com.example.tattooartistbackend.generated.models.TattooWorkPostRequestDto;
import com.example.tattooartistbackend.generated.models.TattooWorksResponseDto;
import com.example.tattooartistbackend.security.SecurityService;
import com.example.tattooartistbackend.user.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TattooWorkService {
    private final TattooWorkRepository tattooWorkRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final String API_KEY = "uTZMvFz8kI2uot9EcqXKz5NHnZpET9UX"; //daily 250 req
    private final SecurityService securityService;

    public TattooWorksResponseDto createTattooWork(TattooWorkPostRequestDto tattooWorkPostRequestDto) {
        var client = userRepository.findById(tattooWorkPostRequestDto.getClientId()).orElseThrow(UserNotFoundException::new);
        var madeBy = securityService.getUser();
        if (!madeBy.isHasArtistPage()) {
            try {
                throw new UserArtistPageNotFoundException();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
//        var convertedPrice = getConvertedPrice(tattooWorkPostRequestDto.getCurrency(), tattooWorkPostRequestDto.getPrice());
        var convertedPrice = BigDecimal.valueOf(40);
        return TattooWork.toTattooWorksResponseDto(tattooWorkRepository.save(TattooWork.fromTattooWorkPostRequest(tattooWorkPostRequestDto, client, madeBy, convertedPrice)));
    }

    private BigDecimal getConvertedPrice(Currency currency, BigDecimal price) {
        var url = "https://api.apilayer.com/exchangerates_data/convert?to=EUR&from=" + currency + "&amount=" + price;
        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", API_KEY);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        var body = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class).getBody();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(body);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return BigDecimal.valueOf(Long.parseLong(jsonNode.get("result").asText()));
    }

    public void deleteTattooWork(UUID id) {
        var authenticatedUser = securityService.getUser();
        var tattooWork = tattooWorkRepository.findById(id).orElseThrow(ReviewNotFoundException::new);
        System.out.println(authenticatedUser.getId());
        System.out.println(tattooWork.getMadeBy().getId());
        if (authenticatedUser.getId().equals(tattooWork.getMadeBy().getId())) {
            tattooWorkRepository.deleteById(id);
        }else {
            throw new NotOwnerOfEntityException("only the owner can delete the tattooWork!");
        }
    }

    public List<TattooWorksResponseDto> getAllTattooWorks(Integer page, Integer size, BigDecimal price, String country) {
        Pageable pageable = PageRequest.of(page, size);
        return tattooWorkRepository.findAllByPriceGreaterThan(price, pageable)
                .getContent()
                .stream()
                .map(TattooWork::toTattooWorksResponseDto)
                .collect(Collectors.toList());
    }


    public TattooWorksResponseDto patchTattooWork(UUID id, TattooWorkPatchRequestDto tattooWorkPatchRequestDto) {
        var tattooWork = tattooWorkRepository.findById(id).orElseThrow(TattooWorkNotFoundException::new);
        var authenticatedUser = securityService.getUser();

        System.out.println(authenticatedUser.getId());
        System.out.println(tattooWork.getMadeBy().getId());

        if (authenticatedUser.getId().toString() == tattooWork.getMadeBy().getId().toString()) {
            throw new NotOwnerOfEntityException("only the owner can edit the tattooWork!");
        }
        tattooWork.setDescription(tattooWorkPatchRequestDto.getDescription());
        tattooWork.setPrice(tattooWorkPatchRequestDto.getPrice());
        tattooWork.setCurrency(tattooWorkPatchRequestDto.getCurrency());
        tattooWork.setPhotos(tattooWorkPatchRequestDto.getPhotos());
        tattooWork.setCoverPhoto(tattooWorkPatchRequestDto.getCoverPhoto());

//        var convertedPrice = getConvertedPrice(tattooWorkPostRequestDto.getCurrency(), tattooWorkPostRequestDto.getPrice());
        var convertedPrice = BigDecimal.valueOf(40);
        tattooWork.setConvertedPriceValue(convertedPrice);
        tattooWorkRepository.save(tattooWork);

        return TattooWork.toTattooWorksResponseDto(tattooWork);
    }

    public TattooWorksResponseDto getTattooWorkById(UUID id) {
        var tattooWork = tattooWorkRepository.findById(id).orElseThrow(TattooWorkNotFoundException::new);
        return TattooWork.toTattooWorksResponseDto(tattooWork);
    }
}
